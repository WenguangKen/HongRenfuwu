package com.athlunakms.webhook.service;

import com.athlunakms.webhook.entity.WebhookEvent;
import com.athlunakms.webhook.processor.WebhookProcessor;
import com.athlunakms.webhook.repository.SystemConfigRepository;
import com.athlunakms.webhook.repository.WebhookEventRepository;
import com.athlunakms.webhook.security.AESEncryptionService;
import com.athlunakms.webhook.sse.SseEventPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
    private final WebhookEventRepository webhookEventRepository;
    private final ObjectMapper objectMapper;
    private final List<WebhookProcessor> processors;
    private final SseEventPublisher sseEventPublisher;
    @Value(value="${shopify.webhook.secret:default-secret}")
    private String webhookSecret;
    @Autowired
    @Lazy
    private WebhookService self;
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    @Autowired
    private AESEncryptionService encryptionService;

    public WebhookService(WebhookEventRepository webhookEventRepository, ObjectMapper objectMapper, List<WebhookProcessor> processors, SseEventPublisher sseEventPublisher) {
        this.webhookEventRepository = webhookEventRepository;
        this.objectMapper = objectMapper;
        this.processors = processors;
        this.sseEventPublisher = sseEventPublisher;
        log.info("WebhookService initialized with {} processors", processors.size());
    }

    private String getEffectiveWebhookSecret() {
        try {
            return this.systemConfigRepository.findByConfigKey("shopify_webhook_secret").map(config -> {
                String val = config.getConfigValue();
                if (val != null && !val.isEmpty() && Boolean.TRUE.equals(config.getIsEncrypted())) {
                    try {
                        return this.encryptionService.decrypt(val);
                    }
                    catch (Exception e) {
                        log.warn("Failed to decrypt webhook secret from DB, falling back to default", e);
                        return null;
                    }
                }
                return val;
            }).filter(val -> val != null && !val.isEmpty()).orElse(this.webhookSecret);
        }
        catch (Exception e) {
            log.warn("Failed to load webhook secret from DB, using default", e);
            return this.webhookSecret;
        }
    }

    public boolean verifyWebhook(String payload, String hmacHeader) {
        if (hmacHeader == null || hmacHeader.isEmpty()) {
            log.warn("Missing HMAC header");
            return false;
        }
        try {
            String secret = this.getEffectiveWebhookSecret();
            if (secret == null || secret.isEmpty()) {
                log.error("No Shopify Webhook Secret configured!");
                return false;
            }
            byte[] hmacBytes = new HmacUtils("HmacSHA256", secret).hmac(payload);
            String calculatedHmac = Base64.encodeBase64String(hmacBytes);
            boolean valid = calculatedHmac.equals(hmacHeader);
            if (!valid) {
                log.warn("HMAC verification failed. Expected: {}, Got: {}", calculatedHmac, hmacHeader);
            }
            return valid;
        }
        catch (Exception e) {
            log.error("Error verifying webhook HMAC", e);
            return false;
        }
    }

    @Transactional
    public Long processWebhook(String topic, String shopDomain, String apiVersion, String webhookId, String payload) {
        log.info("Processing webhook: topic={}, shop={}, webhookId={}", topic, shopDomain, webhookId);
        if (webhookId != null && this.webhookEventRepository.findByWebhookId(webhookId).isPresent()) {
            log.info("Webhook already processed: {}", webhookId);
            return null;
        }
        String resourceId = this.extractResourceId(topic, payload);
        WebhookEvent event = new WebhookEvent();
        event.setTopic(topic);
        event.setShopDomain(shopDomain);
        event.setApiVersion(apiVersion);
        event.setWebhookId(webhookId);
        event.setResourceId(resourceId);
        event.setPayload(payload);
        event.setStatus("PENDING");
        WebhookEvent saved = this.webhookEventRepository.save(event);
        log.info("Webhook event saved: id={}", saved.getId());
        this.self.processEventAsync(saved, topic, shopDomain, payload);
        return saved.getId();
    }

    private String extractResourceId(String topic, String payload) {
        try {
            JsonNode root = this.objectMapper.readTree(payload);
            return root.has("id") ? root.get("id").asText() : null;
        }
        catch (Exception e) {
            log.error("Error extracting resource ID from payload", e);
            return null;
        }
    }

    @Async
    public void processEventAsync(WebhookEvent event, String topic, String shopDomain, String payload) {
        try {
            JsonNode payloadJson = this.objectMapper.readTree(payload);
            WebhookProcessor processor = this.findProcessor(topic);
            if (processor != null) {
                log.info("Dispatching topic={} to {}", topic, processor.getClass().getSimpleName());
                processor.process(topic, shopDomain, payloadJson);
                this.markEventProcessed(event, "Processed by " + processor.getClass().getSimpleName());
                this.publishSseEvent(topic, processor.getCategory(), payloadJson, event.getId());
            } else {
                log.warn("No processor found for topic: {}", topic);
                this.markEventProcessed(event, "No processor found, skipped");
            }
        }
        catch (Exception e) {
            log.error("Error processing webhook event: {}", event.getId(), e);
            this.markEventFailed(event, e.getMessage());
        }
    }

    private void publishSseEvent(String topic, String category, JsonNode payload, Long eventId) {
        try {
            HashMap<String, Object> eventData = new HashMap<String, Object>();
            eventData.put("topic", topic);
            eventData.put("eventId", eventId);
            if (payload.has("id")) {
                eventData.put("resourceId", payload.get("id").asText());
            }
            if (payload.has("title")) {
                eventData.put("title", payload.get("title").asText());
            }
            if (payload.has("name")) {
                eventData.put("name", payload.get("name").asText());
            }
            this.sseEventPublisher.publishWebhookEvent(topic, category, eventData);
        }
        catch (Exception e) {
            log.warn("Failed to publish SSE event: {}", e.getMessage());
        }
    }

    private WebhookProcessor findProcessor(String topic) {
        for (WebhookProcessor processor : this.processors) {
            if (!processor.supports(topic)) continue;
            return processor;
        }
        return null;
    }

    @Transactional
    public void markEventProcessed(WebhookEvent event, String message) {
        event.setStatus("PROCESSED");
        event.setResultMessage(message);
        event.setProcessedAt(LocalDateTime.now());
        this.webhookEventRepository.save(event);
    }

    @Transactional
    public void markEventFailed(WebhookEvent event, String errorMessage) {
        event.setStatus("FAILED");
        event.setResultMessage(errorMessage);
        event.setRetryCount(Integer.valueOf(event.getRetryCount() + 1));
        this.webhookEventRepository.save(event);
    }

    public List<WebhookEvent> getPendingEvents(int maxRetryCount) {
        return this.webhookEventRepository.findByStatusAndRetryCountLessThanOrderByReceivedAtAsc("PENDING", Integer.valueOf(maxRetryCount));
    }

    public List<WebhookEvent> getFailedEvents() {
        return this.webhookEventRepository.findByStatusOrderByReceivedAtAsc("FAILED");
    }

    public List<String> getRegisteredProcessors() {
        return this.processors.stream().map(p -> p.getClass().getSimpleName() + " -> " + p.getCategory()).toList();
    }

    public Map<String, Object> getEndpointStatus(String category) {
        String prefix = switch (category) {
            case "products" -> "products/";
            case "inventory" -> "inventory_levels/";
            case "orders" -> "orders/";
            case "discounts" -> "discounts/";
            default -> category;
        };
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24L);
        if ("inventory".equals(category)) {
            Optional<WebhookEvent> event = this.webhookEventRepository.findFirstByTopicStartingWithOrderByReceivedAtDesc("variants/");
            if (event.isEmpty()) {
                event = this.webhookEventRepository.findFirstByTopicStartingWithOrderByReceivedAtDesc("inventory_levels/");
            }
            if (event.isPresent()) {
                WebhookEvent webEvent = event.get();
                boolean isActive = webEvent.getReceivedAt().isAfter(cutoff);
                return Map.of("status", isActive ? "active" : "inactive", "lastEventAt", webEvent.getReceivedAt(), "lastPayload", webEvent.getPayload() != null ? webEvent.getPayload() : "");
            }
        } else {
            Optional<WebhookEvent> event = this.webhookEventRepository.findFirstByTopicStartingWithOrderByReceivedAtDesc(prefix);
            if (event.isPresent()) {
                WebhookEvent webEvent = event.get();
                boolean isActive = webEvent.getReceivedAt().isAfter(cutoff);
                return Map.of("status", isActive ? "active" : "inactive", "lastEventAt", webEvent.getReceivedAt(), "lastPayload", webEvent.getPayload() != null ? webEvent.getPayload() : "");
            }
        }
        return Map.of("status", "unknown");
    }
}

