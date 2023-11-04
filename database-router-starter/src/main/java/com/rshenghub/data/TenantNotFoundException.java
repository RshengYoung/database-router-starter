package com.rshenghub.data;

public class TenantNotFoundException extends TenantException {

    public TenantNotFoundException(String tenantId) {
        super("TenantID '" + tenantId + "' not found.");
    }

}
