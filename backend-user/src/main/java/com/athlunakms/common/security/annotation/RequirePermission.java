package com.athlunakms.common.security.annotation;

import com.athlunakms.common.security.annotation.RequirePermission;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    public String[] value();

    public LogicalType logical() default LogicalType.OR;

    public static enum LogicalType {
        OR,
        AND;
    }
}

