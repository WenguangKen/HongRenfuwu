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
@Table(name="order_refunds")
public class OrderRefund {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id")
    private Long orderId;
    @Column(name="shopify_refund_id", unique=true)
    private Long shopifyRefundId;
    @Column(name="shopify_order_id")
    private String shopifyOrderId;
    @Column(name="amount")
    private BigDecimal amount;
    @Column(name="currency")
    private String currency;
    @Column(name="reason")
    private String reason;
    @Column(name="note", columnDefinition="TEXT")
    private String note;
    @Column(name="restock")
    private Boolean restock;
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

    public Long getShopifyRefundId() {
        return this.shopifyRefundId;
    }

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getReason() {
        return this.reason;
    }

    public String getNote() {
        return this.note;
    }

    public Boolean getRestock() {
        return this.restock;
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

    public void setShopifyRefundId(Long shopifyRefundId) {
        this.shopifyRefundId = shopifyRefundId;
    }

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setRestock(Boolean restock) {
        this.restock = restock;
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
        if (!(o instanceof OrderRefund)) {
            return false;
        }
        OrderRefund other = (OrderRefund)o;
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
        Long this$shopifyRefundId = this.getShopifyRefundId();
        Long other$shopifyRefundId = other.getShopifyRefundId();
        if (this$shopifyRefundId == null ? other$shopifyRefundId != null : !((Object)this$shopifyRefundId).equals(other$shopifyRefundId)) {
            return false;
        }
        Boolean this$restock = this.getRestock();
        Boolean other$restock = other.getRestock();
        if (this$restock == null ? other$restock != null : !((Object)this$restock).equals(other$restock)) {
            return false;
        }
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
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
        String this$reason = this.getReason();
        String other$reason = other.getReason();
        if (this$reason == null ? other$reason != null : !this$reason.equals(other$reason)) {
            return false;
        }
        String this$note = this.getNote();
        String other$note = other.getNote();
        if (this$note == null ? other$note != null : !this$note.equals(other$note)) {
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
        return other instanceof OrderRefund;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Long $shopifyRefundId = this.getShopifyRefundId();
        result = result * 59 + ($shopifyRefundId == null ? 43 : ((Object)$shopifyRefundId).hashCode());
        Boolean $restock = this.getRestock();
        result = result * 59 + ($restock == null ? 43 : ((Object)$restock).hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $reason = this.getReason();
        result = result * 59 + ($reason == null ? 43 : $reason.hashCode());
        String $note = this.getNote();
        result = result * 59 + ($note == null ? 43 : $note.hashCode());
        LocalDateTime $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : ((Object)$processedAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "OrderRefund(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyRefundId=" + this.getShopifyRefundId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", reason=" + this.getReason() + ", note=" + this.getNote() + ", restock=" + this.getRestock() + ", processedAt=" + this.getProcessedAt() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

