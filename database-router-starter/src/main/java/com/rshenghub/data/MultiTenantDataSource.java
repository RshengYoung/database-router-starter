package com.rshenghub.data;

import java.io.Closeable;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.Assert;

import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.UtilityElf.DefaultThreadFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MultiTenantDataSource extends AbstractDataSource
        implements InitializingBean, Closeable, ApplicationListener<ApplicationReadyEvent> {

    private static final String DEFAULT_TENANT = "_detault";
    private Boolean isApplicationReady = false;

    private ScheduledExecutorService houseKeepingExecutorService;
    private ScheduledExecutorService closerExecutorService;
    private ScheduledFuture<?> houseKeeperTask;
    private ScheduledFuture<?> closerTask;

    private Map<String, TenantDataSourceKeeper> tenantKeeper = new ConcurrentHashMap<>();
    private Queue<TenantDataSourceKeeper> evictedKeeper = new ConcurrentLinkedQueue<>();

    private String tenantDatasourcePath;

    private final TenantSelector<?> tenantSelector;
    private final ResourceLoader resourceLoader;

    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    @Override
    public void close() {
        for (var keeper : tenantKeeper.values()) {
            keeper.close();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.isApplicationReady = true;
    }

    public synchronized void shutdown() {
        if (Objects.nonNull(this.houseKeeperTask)) {
            this.houseKeeperTask.cancel(false);
            this.houseKeeperTask = null;
        }
        if (Objects.nonNull(this.closerTask)) {
            this.closerTask.cancel(false);
            this.closerTask = null;
        }
        this.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.tenantDatasourcePath, "Property 'tenantDatasourcePath' can not be null");
        this.houseKeepingExecutorService = initializeHouseKeepingExecutorService("Tenant-Keeper");
        this.closerExecutorService = initializeHouseKeepingExecutorService("Tenant-Closer");

        this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L,
                TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS);

        this.closerTask = this.closerExecutorService.scheduleWithFixedDelay(new Closer(), 130L,
                TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS);

        this.tenantKeeper.put(DEFAULT_TENANT, defaultKeeper());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }

    public void setTenantDatasourcePath(String path) {
        this.tenantDatasourcePath = path;
    }

    protected DataSource determineTargetDataSource() {
        var tenant = determineCurrentLookupKey();
        var tenantId = tenant.getTenantId();
        var keeper = this.tenantKeeper.computeIfAbsent(tenantId, t -> new TenantDataSourceKeeper(checkTenantExists(t)));
        keeper.updateLastAccessTime();
        var datasource = keeper.getDatasource();
        return datasource;
    }

    protected TenantContext determineCurrentLookupKey() {
        if (!isApplicationReady) {
            return new TenantContext(DEFAULT_TENANT);
        }

        return Optional.ofNullable(tenantSelector.determineCurrentLookupKey())
                .orElseThrow(TenantNotDefinedException::new);
    }

    private Properties checkTenantExists(String tenantId) {
        var tenantResource = resourceLoader.getResource(Path.of(tenantDatasourcePath, tenantId + ".yml").toString());
        if (!tenantResource.exists()) {
            throw new TenantNotFoundException(tenantId);
        }

        var yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(tenantResource);
        return Objects.requireNonNull(yaml.getObject());
    }

    private ScheduledExecutorService initializeHouseKeepingExecutorService(String threadName) {
        final var threadFactory = new DefaultThreadFactory(threadName, true);
        final var executor = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

    private final class HouseKeeper implements Runnable {

        @Override
        public void run() {
            for (String tenantId : tenantKeeper.keySet()) {
                var keeper = tenantKeeper.get(tenantId);
                if (Objects.isNull(keeper)) {
                    break;
                }

                if (keeper.isTimeout()) {
                    log.warn(
                            "MultiTenant HouseKeeping - Thread starvation or clock leap detected (housekeeper delta={}).",
                            ClockSource.CLOCK.elapsedDisplayString0(
                                    keeper.getLastAccessTime().atZone(ZoneId.systemDefault()).toEpochSecond(),
                                    ZonedDateTime.now().toEpochSecond()));
                    tenantKeeper.remove(tenantId);
                    evictedKeeper.add(keeper);

                    keeper = null;
                }
            }
        }
    }

    private final Long waitCloseTime = 10000L;

    private final class Closer implements Runnable {

        @Override
        public void run() {
            while (!evictedKeeper.isEmpty()) {
                var keeper = evictedKeeper.poll();
                log.info("Close Tenant '{}' keeper", keeper.getTenantd());
                keeper.close();

                var startTime = System.currentTimeMillis();

                while (startTime + waitCloseTime > System.currentTimeMillis() && !keeper.isClosed()) {
                    // Wait datasource state until after waitCloseTime
                }

                if (!keeper.isClosed()) {
                    log.warn("Wait {}  30 seconds, but keeper is not closed.", keeper.getTenantd());
                    evictedKeeper.add(keeper);
                } else {
                    log.warn(
                            "MultiTenant Closer - Thread starvation or clock leap detected (closer delta={}).",
                            ClockSource.CLOCK.elapsedDisplayString0(startTime, System.currentTimeMillis()));
                    keeper = null;
                }
            }
        }

    }

    private TenantDataSourceKeeper defaultKeeper() {
        return new TenantDataSourceKeeper(defaultKeeperProperties());
    }

    private Properties defaultKeeperProperties() {
        Properties props = new Properties();
        props.put("tenant", "_default");
        props.put("idle-timeout", "0s");
        props.put("datasource.driverClassName", "org.h2.Driver");
        props.put("datasource.jdbcUrl", "jdbc:h2:mem:_default;INIT=CREATE SCHEMA IF NOT EXISTS BU");
        return props;
    }

}
