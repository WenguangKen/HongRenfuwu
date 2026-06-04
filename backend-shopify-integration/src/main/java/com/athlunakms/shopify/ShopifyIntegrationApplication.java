package com.athlunakms.shopify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages={"com.athlunakms.shopify", "com.athlunakms.user.repository"})
@EntityScan(basePackages={"com.athlunakms.shopify", "com.athlunakms.user.entity"})
public class ShopifyIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopifyIntegrationApplication.class, (String[])args);
    }
}

