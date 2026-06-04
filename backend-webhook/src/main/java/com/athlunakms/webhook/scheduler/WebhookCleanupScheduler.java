package com.athlunakms.webhook.scheduler;

import com.athlunakms.webhook.repository.WebhookEventRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WebhookCleanupScheduler {
    private static final Logger log = LoggerFactory.getLogger(WebhookCleanupScheduler.class);
    private final WebhookEventRepository webhookEventRepository;

    @Scheduled(cron="0 0 3 * * ?")
    @Transactional
    public void cleanupOldEvents() {
        log.info("Starting webhook cleanup task...");
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(3L);
            this.webhookEventRepository.deleteByStatusAndReceivedAtBefore("PROCESSED", cutoff);
            log.info("Webhook cleanup task completed. Deleted 'PROCESSED' events before {}", cutoff);
        }
        catch (Exception e) {
            log.error("Webhook cleanup task failed", e);
        }
    }

    public WebhookCleanupScheduler(WebhookEventRepository webhookEventRepository) {
        this.webhookEventRepository = webhookEventRepository;
    }
}

