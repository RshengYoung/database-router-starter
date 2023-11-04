package com.rshenghub.data;

public abstract class TenantException extends RuntimeException {

    public TenantException() {
        super();
    }

    public TenantException(String message) {
        super(message);
    }

}
