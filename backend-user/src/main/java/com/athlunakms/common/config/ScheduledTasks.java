package com.athlunakms.common.config;

import com.athlunakms.auth.repository.CaptchaRecordRepository;
import com.athlunakms.auth.service.SessionService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final CaptchaRecordRepository captchaRecordRepository;
    private final SessionService sessionService;

    @Scheduled(fixedRate=3600000L)
    @Transactional
    public void cleanupExpiredCaptchas() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cleanupTime = now.minusHours(1L);
            this.captchaRecordRepository.cleanupExpiredCaptchas(now, cleanupTime);
            log.debug("\u6e05\u7406\u8fc7\u671f\u9a8c\u8bc1\u7801\u5b8c\u6210");
        }
        catch (Exception e) {
            log.error("\u6e05\u7406\u8fc7\u671f\u9a8c\u8bc1\u7801\u5931\u8d25", (Throwable)e);
        }
    }

    @Scheduled(fixedRate=300000L)
    public void cleanupExpiredSessions() {
        try {
            this.sessionService.cleanupExpiredSessions();
            log.debug("\u6e05\u7406\u8d85\u65f6\u4f1a\u8bdd\u5b8c\u6210");
        }
        catch (Exception e) {
            log.error("\u6e05\u7406\u8d85\u65f6\u4f1a\u8bdd\u5931\u8d25", (Throwable)e);
        }
    }

    public ScheduledTasks(CaptchaRecordRepository captchaRecordRepository, SessionService sessionService) {
        this.captchaRecordRepository = captchaRecordRepository;
        this.sessionService = sessionService;
    }
}

