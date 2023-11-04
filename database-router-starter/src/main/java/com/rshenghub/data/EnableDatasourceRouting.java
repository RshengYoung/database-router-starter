package com.rshenghub.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ DatabaseRoutingRegistration.class })
public @interface EnableDatasourceRouting {

    @AliasFor("tenantDefineDir")
    String value() default "";

    String beanName() default "datasource";

    String tenantDefineDir() default "";

}
