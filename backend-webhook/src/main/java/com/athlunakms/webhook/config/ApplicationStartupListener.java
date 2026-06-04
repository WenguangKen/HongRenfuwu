package com.athlunakms.webhook.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener
implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupListener.class);
    @Value(value="${server.port:8083}")
    private int serverPort;
    @Value(value="${spring.application.name:backend-webhook}")
    private String applicationName;

    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("========================================");
        log.info("\ud83d\ude80 {} \u5df2\u6210\u529f\u542f\u52a8\uff01", (Object)this.applicationName);
        log.info("\ud83d\udccc \u5f53\u524d\u7248\u672c: v1.0.0");
        log.info("\ud83d\udd0c \u670d\u52a1\u7aef\u53e3: {}", (Object)this.serverPort);
        log.info("\u2705 \u7cfb\u7edf\u5c31\u7eea\uff0c\u6b63\u5728\u63a5\u6536 Webhook \u4e8b\u4ef6");
        log.info("========================================");
    }
}

