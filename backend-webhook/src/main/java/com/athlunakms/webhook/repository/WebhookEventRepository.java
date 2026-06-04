package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.WebhookEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEventRepository
extends JpaRepository<WebhookEvent, Long> {
    public Optional<WebhookEvent> findByWebhookId(String var1);

    public List<WebhookEvent> findByTopicOrderByReceivedAtDesc(String var1);

    public List<WebhookEvent> findByStatusOrderByReceivedAtAsc(String var1);

    public List<WebhookEvent> findByShopDomainAndTopicOrderByReceivedAtDesc(String var1, String var2);

    public List<WebhookEvent> findByStatusAndRetryCountLessThanOrderByReceivedAtAsc(String var1, Integer var2);

    public Optional<WebhookEvent> findFirstByTopicStartingWithOrderByReceivedAtDesc(String var1);

    public void deleteByStatusAndReceivedAtBefore(String var1, LocalDateTime var2);
}

