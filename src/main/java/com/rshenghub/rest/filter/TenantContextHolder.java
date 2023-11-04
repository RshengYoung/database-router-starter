package com.rshenghub.rest.filter;

import com.rshenghub.data.TenantContext;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CURRENT_CONTEXT = new ThreadLocal<>();

    public static TenantContext getContext() {
        return CURRENT_CONTEXT.get();
    }

    public static void setContext(TenantContext context) {
        CURRENT_CONTEXT.set(context);
    }

}
