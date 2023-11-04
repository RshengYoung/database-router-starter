package com.rshenghub.rest.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rshenghub.data.TenantException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "com.rshenghub")
public class TenantExceptionHandler {

    @ExceptionHandler(TenantException.class)
    public ResponseEntity<Map<?, ?>> tenantExceptionHandle(HttpServletRequest request, TenantException ex) {
        var body = Map.of("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), //
                "status", 400, //
                "error", ex.getMessage(), //
                "requestId", request.getRequestId(), //
                "path", request.getRequestURI() //
        );
        return ResponseEntity.badRequest().body(body);
    }

}
