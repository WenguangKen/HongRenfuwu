package com.athlunakms.influencer.config;

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
    @Value(value="${spring.application.name:Athluna KMS Influencer}")
    private String applicationName;
    @Value(value="${app.version:1.0.4}")
    private String version;
    @Value(value="${server.port:8081}")
    private String port;

    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("========================================");
        log.info("\ud83d\ude80 {} \u5df2\u6210\u529f\u542f\u52a8\uff01", (Object)this.applicationName);
        log.info("\ud83d\udccc \u5f53\u524d\u7248\u672c: v{}", (Object)this.version);
        log.info("\ud83c\udf10 \u670d\u52a1\u7aef\u53e3: {}", (Object)this.port);
        log.info("\u2705 \u7cfb\u7edf\u5c31\u7eea\uff0c\u53ef\u4ee5\u5f00\u59cb\u4f7f\u7528\u4e86");
        log.info("========================================");
    }
}

