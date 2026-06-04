package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="order_transactions")
public class OrderTransaction {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id")
    private Long orderId;
    @Column(name="shopify_transaction_id", unique=true)
    private Long shopifyTransactionId;
    @Column(name="shopify_order_id")
    private String shopifyOrderId;
    @Column(name="kind")
    private String kind;
    @Column(name="gateway")
    private String gateway;
    @Column(name="status")
    private String status;
    @Column(name="amount")
    private BigDecimal amount;
    @Column(name="currency")
    private String currency;
    @Column(name="authorization")
    private String authorization;
    @Column(name="parent_id")
    private Long parentId;
    @Column(name="error_code")
    private String errorCode;
    @Column(name="message", columnDefinition="TEXT")
    private String message;
    @Column(name="processed_at")
    private LocalDateTime processedAt;
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public Long getShopifyTransactionId() {
        return this.shopifyTransactionId;
    }

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public String getKind() {
        return this.kind;
    }

    public String getGateway() {
        return this.gateway;
    }

    public String getStatus() {
        return this.status;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getAuthorization() {
        return this.authorization;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getProcessedAt() {
        return this.processedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setShopifyTransactionId(Long shopifyTransactionId) {
        this.shopifyTransactionId = shopifyTransactionId;
    }

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderTransaction)) {
            return false;
        }
        OrderTransaction other = (OrderTransaction)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$orderId = this.getOrderId();
        Long other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !((Object)this$orderId).equals(other$orderId)) {
            return false;
        }
        Long this$shopifyTransactionId = this.getShopifyTransactionId();
        Long other$shopifyTransactionId = other.getShopifyTransactionId();
        if (this$shopifyTransactionId == null ? other$shopifyTransactionId != null : !((Object)this$shopifyTransactionId).equals(other$shopifyTransactionId)) {
            return false;
        }
        Long this$parentId = this.getParentId();
        Long other$parentId = other.getParentId();
        if (this$parentId == null ? other$parentId != null : !((Object)this$parentId).equals(other$parentId)) {
            return false;
        }
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
            return false;
        }
        String this$kind = this.getKind();
        String other$kind = other.getKind();
        if (this$kind == null ? other$kind != null : !this$kind.equals(other$kind)) {
            return false;
        }
        String this$gateway = this.getGateway();
        String other$gateway = other.getGateway();
        if (this$gateway == null ? other$gateway != null : !this$gateway.equals(other$gateway)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        BigDecimal this$amount = this.getAmount();
        BigDecimal other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !((Object)this$amount).equals(other$amount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$authorization = this.getAuthorization();
        String other$authorization = other.getAuthorization();
        if (this$authorization == null ? other$authorization != null : !this$authorization.equals(other$authorization)) {
            return false;
        }
        String this$errorCode = this.getErrorCode();
        String other$errorCode = other.getErrorCode();
        if (this$errorCode == null ? other$errorCode != null : !this$errorCode.equals(other$errorCode)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        LocalDateTime this$processedAt = this.getProcessedAt();
        LocalDateTime other$processedAt = other.getProcessedAt();
        if (this$processedAt == null ? other$processedAt != null : !((Object)this$processedAt).equals(other$processedAt)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderTransaction;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Long $shopifyTransactionId = this.getShopifyTransactionId();
        result = result * 59 + ($shopifyTransactionId == null ? 43 : ((Object)$shopifyTransactionId).hashCode());
        Long $parentId = this.getParentId();
        result = result * 59 + ($parentId == null ? 43 : ((Object)$parentId).hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $kind = this.getKind();
        result = result * 59 + ($kind == null ? 43 : $kind.hashCode());
        String $gateway = this.getGateway();
        result = result * 59 + ($gateway == null ? 43 : $gateway.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $authorization = this.getAuthorization();
        result = result * 59 + ($authorization == null ? 43 : $authorization.hashCode());
        String $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        LocalDateTime $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : ((Object)$processedAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "OrderTransaction(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyTransactionId=" + this.getShopifyTransactionId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", kind=" + this.getKind() + ", gateway=" + this.getGateway() + ", status=" + this.getStatus() + ", amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", authorization=" + this.getAuthorization() + ", parentId=" + this.getParentId() + ", errorCode=" + this.getErrorCode() + ", message=" + this.getMessage() + ", processedAt=" + this.getProcessedAt() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

