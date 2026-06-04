package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name="influencer_commission_order", indexes={
    @Index(name="idx_comord_influencer", columnList="influencer_id"),
    @Index(name="idx_comord_status", columnList="settlement_status"),
    @Index(name="idx_comord_discount", columnList="discount_code")
})
public class InfluencerCommissionOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="conversion_order_id", nullable=false, unique=true)
    private Long conversionOrderId;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="influencer_name", length=200)
    private String influencerName;
    @Column(name="order_name", length=50)
    private String orderName;
    @Column(name="discount_code", length=100)
    private String discountCode;
    @Column(name="order_total", precision=12, scale=2, nullable=false)
    private BigDecimal orderTotal = BigDecimal.ZERO;
    @Column(name="shipping_amount", precision=12, scale=2, nullable=false)
    private BigDecimal shippingAmount = BigDecimal.ZERO;
    @Column(name="refund_amount", precision=12, scale=2, nullable=false)
    private BigDecimal refundAmount = BigDecimal.ZERO;
    @Column(name="commissionable_amount", precision=12, scale=2, nullable=false)
    private BigDecimal commissionableAmount = BigDecimal.ZERO;
    @Column(name="commission_rate", precision=5, scale=2, nullable=false)
    private BigDecimal commissionRate = BigDecimal.ZERO;
    @Column(name="commission_amount", precision=12, scale=2, nullable=false)
    private BigDecimal commissionAmount = BigDecimal.ZERO;
    @Column(length=10)
    private String currency = "USD";
    @Column(name="settlement_status", length=20, nullable=false)
    private String settlementStatus = "pending";
    @Column(name="settled_at")
    private LocalDateTime settledAt;
    @Column(name="delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name="tracking_number", length=100)
    private String trackingNumber;
    @Column(name="items_summary", columnDefinition="TEXT")
    private String itemsSummary;
    @Column(name="paid_at")
    private LocalDateTime paidAt;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateCommission() {
        this.commissionableAmount = this.orderTotal.subtract(this.shippingAmount != null ? this.shippingAmount : BigDecimal.ZERO).subtract(this.refundAmount != null ? this.refundAmount : BigDecimal.ZERO);
        if (this.commissionableAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.commissionableAmount = BigDecimal.ZERO;
        }
        if (this.commissionRate != null && this.commissionRate.compareTo(BigDecimal.ZERO) > 0) {
            this.commissionAmount = this.commissionableAmount.multiply(this.commissionRate).divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
        }
    }

    public Long getId() {
        return this.id;
    }

    public Long getConversionOrderId() {
        return this.conversionOrderId;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public String getOrderName() {
        return this.orderName;
    }

    public String getDiscountCode() {
        return this.discountCode;
    }

    public BigDecimal getOrderTotal() {
        return this.orderTotal;
    }

    public BigDecimal getShippingAmount() {
        return this.shippingAmount;
    }

    public BigDecimal getRefundAmount() {
        return this.refundAmount;
    }

    public BigDecimal getCommissionableAmount() {
        return this.commissionableAmount;
    }

    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getSettlementStatus() {
        return this.settlementStatus;
    }

    public LocalDateTime getSettledAt() {
        return this.settledAt;
    }

    public LocalDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public String getItemsSummary() {
        return this.itemsSummary;
    }

    public LocalDateTime getPaidAt() {
        return this.paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setConversionOrderId(Long conversionOrderId) {
        this.conversionOrderId = conversionOrderId;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public void setCommissionableAmount(BigDecimal commissionableAmount) {
        this.commissionableAmount = commissionableAmount;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public void setSettledAt(LocalDateTime settledAt) {
        this.settledAt = settledAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerCommissionOrder)) {
            return false;
        }
        InfluencerCommissionOrder other = (InfluencerCommissionOrder)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$conversionOrderId = this.getConversionOrderId();
        Long other$conversionOrderId = other.getConversionOrderId();
        if (this$conversionOrderId == null ? other$conversionOrderId != null : !((Object)this$conversionOrderId).equals(other$conversionOrderId)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        String this$orderName = this.getOrderName();
        String other$orderName = other.getOrderName();
        if (this$orderName == null ? other$orderName != null : !this$orderName.equals(other$orderName)) {
            return false;
        }
        String this$discountCode = this.getDiscountCode();
        String other$discountCode = other.getDiscountCode();
        if (this$discountCode == null ? other$discountCode != null : !this$discountCode.equals(other$discountCode)) {
            return false;
        }
        BigDecimal this$orderTotal = this.getOrderTotal();
        BigDecimal other$orderTotal = other.getOrderTotal();
        if (this$orderTotal == null ? other$orderTotal != null : !((Object)this$orderTotal).equals(other$orderTotal)) {
            return false;
        }
        BigDecimal this$shippingAmount = this.getShippingAmount();
        BigDecimal other$shippingAmount = other.getShippingAmount();
        if (this$shippingAmount == null ? other$shippingAmount != null : !((Object)this$shippingAmount).equals(other$shippingAmount)) {
            return false;
        }
        BigDecimal this$refundAmount = this.getRefundAmount();
        BigDecimal other$refundAmount = other.getRefundAmount();
        if (this$refundAmount == null ? other$refundAmount != null : !((Object)this$refundAmount).equals(other$refundAmount)) {
            return false;
        }
        BigDecimal this$commissionableAmount = this.getCommissionableAmount();
        BigDecimal other$commissionableAmount = other.getCommissionableAmount();
        if (this$commissionableAmount == null ? other$commissionableAmount != null : !((Object)this$commissionableAmount).equals(other$commissionableAmount)) {
            return false;
        }
        BigDecimal this$commissionRate = this.getCommissionRate();
        BigDecimal other$commissionRate = other.getCommissionRate();
        if (this$commissionRate == null ? other$commissionRate != null : !((Object)this$commissionRate).equals(other$commissionRate)) {
            return false;
        }
        BigDecimal this$commissionAmount = this.getCommissionAmount();
        BigDecimal other$commissionAmount = other.getCommissionAmount();
        if (this$commissionAmount == null ? other$commissionAmount != null : !((Object)this$commissionAmount).equals(other$commissionAmount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$settlementStatus = this.getSettlementStatus();
        String other$settlementStatus = other.getSettlementStatus();
        if (this$settlementStatus == null ? other$settlementStatus != null : !this$settlementStatus.equals(other$settlementStatus)) {
            return false;
        }
        LocalDateTime this$settledAt = this.getSettledAt();
        LocalDateTime other$settledAt = other.getSettledAt();
        if (this$settledAt == null ? other$settledAt != null : !((Object)this$settledAt).equals(other$settledAt)) {
            return false;
        }
        LocalDateTime this$deliveredAt = this.getDeliveredAt();
        LocalDateTime other$deliveredAt = other.getDeliveredAt();
        if (this$deliveredAt == null ? other$deliveredAt != null : !((Object)this$deliveredAt).equals(other$deliveredAt)) {
            return false;
        }
        String this$trackingNumber = this.getTrackingNumber();
        String other$trackingNumber = other.getTrackingNumber();
        if (this$trackingNumber == null ? other$trackingNumber != null : !this$trackingNumber.equals(other$trackingNumber)) {
            return false;
        }
        String this$itemsSummary = this.getItemsSummary();
        String other$itemsSummary = other.getItemsSummary();
        if (this$itemsSummary == null ? other$itemsSummary != null : !this$itemsSummary.equals(other$itemsSummary)) {
            return false;
        }
        LocalDateTime this$paidAt = this.getPaidAt();
        LocalDateTime other$paidAt = other.getPaidAt();
        if (this$paidAt == null ? other$paidAt != null : !((Object)this$paidAt).equals(other$paidAt)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        return !(this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerCommissionOrder;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $conversionOrderId = this.getConversionOrderId();
        result = result * 59 + ($conversionOrderId == null ? 43 : ((Object)$conversionOrderId).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        String $orderName = this.getOrderName();
        result = result * 59 + ($orderName == null ? 43 : $orderName.hashCode());
        String $discountCode = this.getDiscountCode();
        result = result * 59 + ($discountCode == null ? 43 : $discountCode.hashCode());
        BigDecimal $orderTotal = this.getOrderTotal();
        result = result * 59 + ($orderTotal == null ? 43 : ((Object)$orderTotal).hashCode());
        BigDecimal $shippingAmount = this.getShippingAmount();
        result = result * 59 + ($shippingAmount == null ? 43 : ((Object)$shippingAmount).hashCode());
        BigDecimal $refundAmount = this.getRefundAmount();
        result = result * 59 + ($refundAmount == null ? 43 : ((Object)$refundAmount).hashCode());
        BigDecimal $commissionableAmount = this.getCommissionableAmount();
        result = result * 59 + ($commissionableAmount == null ? 43 : ((Object)$commissionableAmount).hashCode());
        BigDecimal $commissionRate = this.getCommissionRate();
        result = result * 59 + ($commissionRate == null ? 43 : ((Object)$commissionRate).hashCode());
        BigDecimal $commissionAmount = this.getCommissionAmount();
        result = result * 59 + ($commissionAmount == null ? 43 : ((Object)$commissionAmount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $settlementStatus = this.getSettlementStatus();
        result = result * 59 + ($settlementStatus == null ? 43 : $settlementStatus.hashCode());
        LocalDateTime $settledAt = this.getSettledAt();
        result = result * 59 + ($settledAt == null ? 43 : ((Object)$settledAt).hashCode());
        LocalDateTime $deliveredAt = this.getDeliveredAt();
        result = result * 59 + ($deliveredAt == null ? 43 : ((Object)$deliveredAt).hashCode());
        String $trackingNumber = this.getTrackingNumber();
        result = result * 59 + ($trackingNumber == null ? 43 : $trackingNumber.hashCode());
        String $itemsSummary = this.getItemsSummary();
        result = result * 59 + ($itemsSummary == null ? 43 : $itemsSummary.hashCode());
        LocalDateTime $paidAt = this.getPaidAt();
        result = result * 59 + ($paidAt == null ? 43 : ((Object)$paidAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerCommissionOrder(id=" + this.getId() + ", conversionOrderId=" + this.getConversionOrderId() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", orderName=" + this.getOrderName() + ", discountCode=" + this.getDiscountCode() + ", orderTotal=" + this.getOrderTotal() + ", shippingAmount=" + this.getShippingAmount() + ", refundAmount=" + this.getRefundAmount() + ", commissionableAmount=" + this.getCommissionableAmount() + ", commissionRate=" + this.getCommissionRate() + ", commissionAmount=" + this.getCommissionAmount() + ", currency=" + this.getCurrency() + ", settlementStatus=" + this.getSettlementStatus() + ", settledAt=" + this.getSettledAt() + ", deliveredAt=" + this.getDeliveredAt() + ", trackingNumber=" + this.getTrackingNumber() + ", itemsSummary=" + this.getItemsSummary() + ", paidAt=" + this.getPaidAt() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }

    public InfluencerCommissionOrder() {
    }

    public InfluencerCommissionOrder(Long id, Long conversionOrderId, Long influencerId, String influencerName, String orderName, String discountCode, BigDecimal orderTotal, BigDecimal shippingAmount, BigDecimal refundAmount, BigDecimal commissionableAmount, BigDecimal commissionRate, BigDecimal commissionAmount, String currency, String settlementStatus, LocalDateTime settledAt, LocalDateTime deliveredAt, String trackingNumber, String itemsSummary, LocalDateTime paidAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.conversionOrderId = conversionOrderId;
        this.influencerId = influencerId;
        this.influencerName = influencerName;
        this.orderName = orderName;
        this.discountCode = discountCode;
        this.orderTotal = orderTotal;
        this.shippingAmount = shippingAmount;
        this.refundAmount = refundAmount;
        this.commissionableAmount = commissionableAmount;
        this.commissionRate = commissionRate;
        this.commissionAmount = commissionAmount;
        this.currency = currency;
        this.settlementStatus = settlementStatus;
        this.settledAt = settledAt;
        this.deliveredAt = deliveredAt;
        this.trackingNumber = trackingNumber;
        this.itemsSummary = itemsSummary;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

