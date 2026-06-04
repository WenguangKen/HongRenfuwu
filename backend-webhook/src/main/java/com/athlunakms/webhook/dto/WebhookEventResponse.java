package com.athlunakms.webhook.dto;

public class WebhookEventResponse {
    private Long id;
    private String topic;
    private String shopDomain;
    private String resourceId;
    private String status;
    private String resultMessage;
    private Integer retryCount;
    private String receivedAt;
    private String processedAt;

    public Long getId() {
        return this.id;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getShopDomain() {
        return this.shopDomain;
    }

    public String getResourceId() {
        return this.resourceId;
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

    public String getReceivedAt() {
        return this.receivedAt;
    }

    public String getProcessedAt() {
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

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WebhookEventResponse)) {
            return false;
        }
        WebhookEventResponse other = (WebhookEventResponse)o;
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
        String this$resourceId = this.getResourceId();
        String other$resourceId = other.getResourceId();
        if (this$resourceId == null ? other$resourceId != null : !this$resourceId.equals(other$resourceId)) {
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
        String this$receivedAt = this.getReceivedAt();
        String other$receivedAt = other.getReceivedAt();
        if (this$receivedAt == null ? other$receivedAt != null : !this$receivedAt.equals(other$receivedAt)) {
            return false;
        }
        String this$processedAt = this.getProcessedAt();
        String other$processedAt = other.getProcessedAt();
        return !(this$processedAt == null ? other$processedAt != null : !this$processedAt.equals(other$processedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof WebhookEventResponse;
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
        String $resourceId = this.getResourceId();
        result = result * 59 + ($resourceId == null ? 43 : $resourceId.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $resultMessage = this.getResultMessage();
        result = result * 59 + ($resultMessage == null ? 43 : $resultMessage.hashCode());
        String $receivedAt = this.getReceivedAt();
        result = result * 59 + ($receivedAt == null ? 43 : $receivedAt.hashCode());
        String $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : $processedAt.hashCode());
        return result;
    }

    public String toString() {
        return "WebhookEventResponse(id=" + this.getId() + ", topic=" + this.getTopic() + ", shopDomain=" + this.getShopDomain() + ", resourceId=" + this.getResourceId() + ", status=" + this.getStatus() + ", resultMessage=" + this.getResultMessage() + ", retryCount=" + this.getRetryCount() + ", receivedAt=" + this.getReceivedAt() + ", processedAt=" + this.getProcessedAt() + ")";
    }
}

