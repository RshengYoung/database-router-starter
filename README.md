# database-router-starter

## Quick Start
```Java
@EnableJpaRepositories
@EnableTransactionManagement
@EnableDatasourceRouting("file:{TENANT_DATASOURCE_CONFIG_DIR}")
@Configuration
public class MultiTenantDatabaseConfig extends DatabaseRoutingAdapter {

    @Override
    public TenantSelector<TenantContext> tenantSelector() {
        return () -> TenantContextHolder.getContext();
    }

}
``````

## Tenant DataSource Config
```
- {TENANT_DATASOURCE_CONFIG_DIR}/
    - {TENANT_1}.yml
    - {TENANT_2}.yml
    - ...
```
