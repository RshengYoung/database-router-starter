package com.rshenghub.rest.filter;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rshenghub.data.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@WebFilter
public class TenantDispatcherFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var tenantId = request.getHeader("X-TenantID");
        if (Objects.nonNull(tenantId)) {
            var tenant = new TenantContext(tenantId);
            TenantContextHolder.setContext(tenant);
        }
        chain.doFilter(request, response);
    }

}
