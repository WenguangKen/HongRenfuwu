package com.athlunakms.common.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // 自动修复并执行迁移防止由于反编译还原导致的元数据不一致问题
            flyway.repair();
            flyway.migrate();
        };
    }
}
