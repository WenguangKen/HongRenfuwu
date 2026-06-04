package com.athlunakms.webhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages={"com.athlunakms.webhook.repository"})
@EntityScan(basePackages={"com.athlunakms.webhook.entity"})
@EnableScheduling
public class WebhookApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, (String[])args);
    }
}

