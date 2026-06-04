package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="webhook_events")
public class WebhookEvent {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="topic", nullable=false, length=100)
    private String topic;
    @Column(name="shop_domain", nullable=false, length=255)
    private String shopDomain;
    @Column(name="api_version", length=20)
    private String apiVersion;
    @Column(name="webhook_id", length=100)
    private String webhookId;
    @Column(name="resource_id", length=100)
    private String resourceId;
    @Column(name="payload", columnDefinition="LONGTEXT")
    private String payload;
    @Column(name="status", nullable=false, length=20)
    private String status = "PENDING";
    @Column(name="result_message", columnDefinition="TEXT")
    private String resultMessage;
    @Column(name="retry_count")
    private Integer retryCount = 0;
    @Column(name="received_at", nullable=false)
    private LocalDateTime receivedAt;
    @Column(name="processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.receivedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getShopDomain() {
        return this.shopDomain;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public String getWebhookId() {
        return this.webhookId;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public String getPayload() {
        return this.payload;
    }

    public String getStatus() {
        return this.status;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public LocalDateTime getReceivedAt() {
        return this.receivedAt;
    }

    public LocalDateTime getProcessedAt() {
        return this.processedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WebhookEvent)) {
            return false;
        }
        WebhookEvent other = (WebhookEvent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$retryCount = this.getRetryCount();
        Integer other$retryCount = other.getRetryCount();
        if (this$retryCount == null ? other$retryCount != null : !((Object)this$retryCount).equals(other$retryCount)) {
            return false;
        }
        String this$topic = this.getTopic();
        String other$topic = other.getTopic();
        if (this$topic == null ? other$topic != null : !this$topic.equals(other$topic)) {
            return false;
        }
        String this$shopDomain = this.getShopDomain();
        String other$shopDomain = other.getShopDomain();
        if (this$shopDomain == null ? other$shopDomain != null : !this$shopDomain.equals(other$shopDomain)) {
            return false;
        }
        String this$apiVersion = this.getApiVersion();
        String other$apiVersion = other.getApiVersion();
        if (this$apiVersion == null ? other$apiVersion != null : !this$apiVersion.equals(other$apiVersion)) {
            return false;
        }
        String this$webhookId = this.getWebhookId();
        String other$webhookId = other.getWebhookId();
        if (this$webhookId == null ? other$webhookId != null : !this$webhookId.equals(other$webhookId)) {
            return false;
        }
        String this$resourceId = this.getResourceId();
        String other$resourceId = other.getResourceId();
        if (this$resourceId == null ? other$resourceId != null : !this$resourceId.equals(other$resourceId)) {
            return false;
        }
        String this$payload = this.getPayload();
        String other$payload = other.getPayload();
        if (this$payload == null ? other$payload != null : !this$payload.equals(other$payload)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$resultMessage = this.getResultMessage();
        String other$resultMessage = other.getResultMessage();
        if (this$resultMessage == null ? other$resultMessage != null : !this$resultMessage.equals(other$resultMessage)) {
            return false;
        }
        LocalDateTime this$receivedAt = this.getReceivedAt();
        LocalDateTime other$receivedAt = other.getReceivedAt();
        if (this$receivedAt == null ? other$receivedAt != null : !((Object)this$receivedAt).equals(other$receivedAt)) {
            return false;
        }
        LocalDateTime this$processedAt = this.getProcessedAt();
        LocalDateTime other$processedAt = other.getProcessedAt();
        return !(this$processedAt == null ? other$processedAt != null : !((Object)this$processedAt).equals(other$processedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof WebhookEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $retryCount = this.getRetryCount();
        result = result * 59 + ($retryCount == null ? 43 : ((Object)$retryCount).hashCode());
        String $topic = this.getTopic();
        result = result * 59 + ($topic == null ? 43 : $topic.hashCode());
        String $shopDomain = this.getShopDomain();
        result = result * 59 + ($shopDomain == null ? 43 : $shopDomain.hashCode());
        String $apiVersion = this.getApiVersion();
        result = result * 59 + ($apiVersion == null ? 43 : $apiVersion.hashCode());
        String $webhookId = this.getWebhookId();
        result = result * 59 + ($webhookId == null ? 43 : $webhookId.hashCode());
        String $resourceId = this.getResourceId();
        result = result * 59 + ($resourceId == null ? 43 : $resourceId.hashCode());
        String $payload = this.getPayload();
        result = result * 59 + ($payload == null ? 43 : $payload.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $resultMessage = this.getResultMessage();
        result = result * 59 + ($resultMessage == null ? 43 : $resultMessage.hashCode());
        LocalDateTime $receivedAt = this.getReceivedAt();
        result = result * 59 + ($receivedAt == null ? 43 : ((Object)$receivedAt).hashCode());
        LocalDateTime $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : ((Object)$processedAt).hashCode());
        return result;
    }

    public String toString() {
        return "WebhookEvent(id=" + this.getId() + ", topic=" + this.getTopic() + ", shopDomain=" + this.getShopDomain() + ", apiVersion=" + this.getApiVersion() + ", webhookId=" + this.getWebhookId() + ", resourceId=" + this.getResourceId() + ", payload=" + this.getPayload() + ", status=" + this.getStatus() + ", resultMessage=" + this.getResultMessage() + ", retryCount=" + this.getRetryCount() + ", receivedAt=" + this.getReceivedAt() + ", processedAt=" + this.getProcessedAt() + ")";
    }
}

