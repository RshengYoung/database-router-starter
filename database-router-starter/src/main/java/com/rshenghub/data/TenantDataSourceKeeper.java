package com.rshenghub.data;

import java.io.Closeable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.Properties;

import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class TenantDataSourceKeeper implements Closeable {
    private String tenantId;
    private HikariConfig config;
    private HikariDataSource datasource;
    private LocalDateTime lastAccessTime;
    private TemporalAmount idleTimeout;

    public TenantDataSourceKeeper(Properties props) {
        this.tenantId = props.getProperty("tenant");
        this.setIdleTimeout(props.getProperty("idle-timeout", "5m"));
        var datasourceProperties = this.getProperties("datasource", props);

        this.config = new HikariConfig(datasourceProperties);
        this.config.setPoolName("hikari-" + tenantId);
        this.datasource = new HikariDataSource(this.config);
    }

    public void setIdleTimeout(String timeout) {
        // this.setIdleTimeout(Period.parse(timeout));

        if (Character.isUpperCase(timeout.charAt(timeout.length() - 1))) {
            this.idleTimeout = Period.parse("P" + timeout);
        } else {
            this.idleTimeout = Duration.parse("PT" + timeout);
        }
    }

    public final String getTenantd() {
        return this.tenantId;
    }

    public final HikariDataSource getDatasource() {
        return datasource;
    }

    public final void updateLastAccessTime() {
        this.lastAccessTime = LocalDateTime.now();
    }

    public LocalDateTime getLastAccessTime() {
        return this.lastAccessTime;
    }

    public final Boolean isTimeout() {
        if (Objects.isNull(idleTimeout)) {
            return false;
        }

        return getLastAccessTime().plus(idleTimeout).isBefore(LocalDateTime.now());
    }

    private Properties getProperties(String prefix, Properties props) {
        Properties subProps = new Properties();
        for (String property : props.stringPropertyNames()) {
            if (StringUtils.startsWithIgnoreCase(property, prefix)) {
                var key = property.substring(prefix.length() + 1);
                var value = props.getProperty(property);
                subProps.put(key, value);
            }
        }
        return subProps;
    }

    @Override
    public void close() {
        if (Objects.nonNull(this.datasource)) {
            this.datasource.close();
            this.datasource = null;
        }
    }

    public Boolean isClosed() {
        return Objects.isNull(this.datasource) || this.datasource.isClosed();
    }

}
