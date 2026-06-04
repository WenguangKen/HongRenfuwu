package com.athlunakms.shopify.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Order(value = 1)
public class DataSourceAspect {

    @Around("@annotation(transactional)")
    public Object switchDataSource(ProceedingJoinPoint point, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                DataSourceConfig.DataSourceContextHolder.setDataSourceType("slave");
            } else {
                DataSourceConfig.DataSourceContextHolder.setDataSourceType("master");
            }
            return point.proceed();
        } finally {
            DataSourceConfig.DataSourceContextHolder.clearDataSourceType();
        }
    }
}
