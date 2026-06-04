package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="influencer_sample_order", indexes={@Index(name="idx_sample_influencer", columnList="influencer_id")})
public class InfluencerSampleOrder {
    @Id
    private Long id;
    @Column(name="order_id")
    private Long orderId;
    @Column(name="shopify_order_id")
    private String shopifyOrderId;
    @Column(name="shopify_order_number")
    private Long shopifyOrderNumber;
    @Column(name="order_name")
    private String orderName;
    @Column(name="influencer_id")
    private Long influencerId;
    @Column(name="influencer_name")
    private String influencerName;
    @Column(name="total_price")
    private BigDecimal totalPrice;
    private String currency;
    @Column(name="sample_reason")
    private String sampleReason;
    @Column(name="order_created_at")
    private LocalDateTime orderCreatedAt;
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public Long getShopifyOrderNumber() {
        return this.shopifyOrderNumber;
    }

    public String getOrderName() {
        return this.orderName;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getSampleReason() {
        return this.sampleReason;
    }

    public LocalDateTime getOrderCreatedAt() {
        return this.orderCreatedAt;
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

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setShopifyOrderNumber(Long shopifyOrderNumber) {
        this.shopifyOrderNumber = shopifyOrderNumber;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setSampleReason(String sampleReason) {
        this.sampleReason = sampleReason;
    }

    public void setOrderCreatedAt(LocalDateTime orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerSampleOrder)) {
            return false;
        }
        InfluencerSampleOrder other = (InfluencerSampleOrder)o;
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
        Long this$shopifyOrderNumber = this.getShopifyOrderNumber();
        Long other$shopifyOrderNumber = other.getShopifyOrderNumber();
        if (this$shopifyOrderNumber == null ? other$shopifyOrderNumber != null : !((Object)this$shopifyOrderNumber).equals(other$shopifyOrderNumber)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
            return false;
        }
        String this$orderName = this.getOrderName();
        String other$orderName = other.getOrderName();
        if (this$orderName == null ? other$orderName != null : !this$orderName.equals(other$orderName)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        BigDecimal this$totalPrice = this.getTotalPrice();
        BigDecimal other$totalPrice = other.getTotalPrice();
        if (this$totalPrice == null ? other$totalPrice != null : !((Object)this$totalPrice).equals(other$totalPrice)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$sampleReason = this.getSampleReason();
        String other$sampleReason = other.getSampleReason();
        if (this$sampleReason == null ? other$sampleReason != null : !this$sampleReason.equals(other$sampleReason)) {
            return false;
        }
        LocalDateTime this$orderCreatedAt = this.getOrderCreatedAt();
        LocalDateTime other$orderCreatedAt = other.getOrderCreatedAt();
        if (this$orderCreatedAt == null ? other$orderCreatedAt != null : !((Object)this$orderCreatedAt).equals(other$orderCreatedAt)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerSampleOrder;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Long $shopifyOrderNumber = this.getShopifyOrderNumber();
        result = result * 59 + ($shopifyOrderNumber == null ? 43 : ((Object)$shopifyOrderNumber).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $orderName = this.getOrderName();
        result = result * 59 + ($orderName == null ? 43 : $orderName.hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        BigDecimal $totalPrice = this.getTotalPrice();
        result = result * 59 + ($totalPrice == null ? 43 : ((Object)$totalPrice).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $sampleReason = this.getSampleReason();
        result = result * 59 + ($sampleReason == null ? 43 : $sampleReason.hashCode());
        LocalDateTime $orderCreatedAt = this.getOrderCreatedAt();
        result = result * 59 + ($orderCreatedAt == null ? 43 : ((Object)$orderCreatedAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerSampleOrder(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber() + ", orderName=" + this.getOrderName() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", totalPrice=" + this.getTotalPrice() + ", currency=" + this.getCurrency() + ", sampleReason=" + this.getSampleReason() + ", orderCreatedAt=" + this.getOrderCreatedAt() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

