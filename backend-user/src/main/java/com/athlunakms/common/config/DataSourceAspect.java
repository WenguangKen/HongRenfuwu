package com.athlunakms.common.config;

import com.athlunakms.common.config.DataSourceConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Order(value=1)
public class DataSourceAspect {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="@annotation(transactional)")
    public Object switchDataSource(ProceedingJoinPoint point, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                DataSourceConfig.DataSourceContextHolder.setDataSourceType((String)"slave");
            } else {
                DataSourceConfig.DataSourceContextHolder.setDataSourceType((String)"master");
            }
            Object object = point.proceed();
            return object;
        }
        finally {
            DataSourceConfig.DataSourceContextHolder.clearDataSourceType();
        }
    }
}

