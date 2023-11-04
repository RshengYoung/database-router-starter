package com.rshenghub.data;

public class TenantNotDefinedException extends TenantException {

    public TenantNotDefinedException() {
        super("TenantID is not defined.");
    }

}
