package com.athlunakms.eccang.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Value(value="${server.port}")
    private int serverPort;
    @Value(value="${spring.application.name}")
    private String applicationName;

    public void onApplicationEvent(ApplicationReadyEvent event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("========================================");
        log.info("{} \u5df2\u6210\u529f\u542f\u52a8\uff01", (Object)this.applicationName);
        log.info("\u5f53\u524d\u7248\u672c: v1.0.4");
        log.info("\u670d\u52a1\u7aef\u53e3: {}", (Object)this.serverPort);
        log.info("\u7cfb\u7edf\u5c31\u7eea\uff0c\u53ef\u4ee5\u5f00\u59cb\u4f7f\u7528\u4e86");
        log.info("========================================");
    }
}

