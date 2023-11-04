package com.rshenghub.data;

public interface TenantSelector<T extends TenantContext> {

    public T determineCurrentLookupKey();

}
