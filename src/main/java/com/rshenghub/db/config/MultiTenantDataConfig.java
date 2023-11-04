package com.rshenghub.db.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.rshenghub.data.DatabaseRoutingAdapter;
import com.rshenghub.data.EnableDatasourceRouting;
import com.rshenghub.data.TenantContext;
import com.rshenghub.data.TenantSelector;
import com.rshenghub.rest.filter.TenantContextHolder;

@EnableDatasourceRouting("file:bu-datasource")
@EnableJpaRepositories("com.rshenghub.db")
@EnableTransactionManagement
@Configuration
public class MultiTenantDataConfig extends DatabaseRoutingAdapter {

    @Override
    public TenantSelector<TenantContext> tenantSelector() {
        return () -> TenantContextHolder.getContext();
    }

}
