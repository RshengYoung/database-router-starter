package com.rshenghub.data;

import org.springframework.context.annotation.Bean;

public abstract class DatabaseRoutingAdapter {

    @Bean
    public abstract <T extends TenantContext> TenantSelector<T> tenantSelector();

}
