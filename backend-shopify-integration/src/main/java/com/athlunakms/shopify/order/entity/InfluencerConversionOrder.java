package com.athlunakms.shopify.order.entity;

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
@Table(name="influencer_conversion_order", indexes={@Index(name="idx_ico_discount_code", columnList="discount_code"), @Index(name="idx_ico_paid_at", columnList="paid_at"), @Index(name="idx_ico_processed_at", columnList="processed_at"), @Index(name="idx_ico_order_created_at", columnList="order_created_at"), @Index(name="idx_ico_created_influencer", columnList="created_at, influencer_id, total_price")})
public class InfluencerConversionOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id", nullable=false, unique=true)
    private Long orderId;
    @Column(name="shopify_order_id", length=100)
    private String shopifyOrderId;
    @Column(name="shopify_order_number")
    private Long shopifyOrderNumber;
    @Column(name="order_name", length=50)
    private String orderName;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="influencer_name", length=200)
    private String influencerName;
    @Column(name="discount_code", length=100)
    private String discountCode;
    @Column(name="total_price", precision=12, scale=2)
    private BigDecimal totalPrice;
    @Column(name="total_shipping", precision=12, scale=2)
    private BigDecimal totalShipping;
    @Column(name="total_refund", precision=12, scale=2)
    private BigDecimal totalRefund = BigDecimal.ZERO;
    @Column(name="commissionable_amount", precision=12, scale=2)
    private BigDecimal commissionableAmount;
    @Column(length=10)
    private String currency = "USD";
    @Column(name="commission_rate", precision=5, scale=2)
    private BigDecimal commissionRate;
    @Column(name="commission_amount", precision=12, scale=2)
    private BigDecimal commissionAmount;
    @Column(name="commission_status", length=50)
    private String commissionStatus = "pending";
    @Column(name="commission_source", length=20)
    private String commissionSource = "influencer";
    @Column(name="financial_status", length=50)
    private String financialStatus;
    @Column(name="cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name="delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name="in_transit_at")
    private LocalDateTime inTransitAt;
    @Column(name="fulfillment_created_at")
    private LocalDateTime fulfillmentCreatedAt;
    @Column(name="closed_at")
    private LocalDateTime closedAt;
    @Column(name="estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;
    @Column(name="processed_at")
    private LocalDateTime processedAt;
    @Column(name="order_created_at")
    private LocalDateTime orderCreatedAt;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name="calculated_at")
    private LocalDateTime calculatedAt;
    @Column(name="paid_at")
    private LocalDateTime paidAt;
    @Column(name="fulfillment_display_status", length=100)
    private String fulfillmentDisplayStatus;
    @Column(name="fulfillment_status", length=50)
    private String fulfillmentStatus;
    @Column(name="tracking_company", length=200)
    private String trackingCompany;
    @Column(name="tracking_number", length=200)
    private String trackingNumber;
    @Column(name="tracking_url", length=1000)
    private String trackingUrl;
    @Column(name="tracking_events_json", columnDefinition="JSON")
    private String trackingEventsJson;
    @Column(name="recipient_name", length=200)
    private String recipientName;
    @Column(name="recipient_phone", length=50)
    private String recipientPhone;
    @Column(name="recipient_address", columnDefinition="TEXT")
    private String recipientAddress;
    @Column(name="recipient_country", length=100)
    private String recipientCountry;
    @Column(name="customer_email", length=200)
    private String customerEmail;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateCommissionableAmount() {
        BigDecimal price = this.totalPrice != null ? this.totalPrice : BigDecimal.ZERO;
        BigDecimal shipping = this.totalShipping != null ? this.totalShipping : BigDecimal.ZERO;
        BigDecimal refund = this.totalRefund != null ? this.totalRefund : BigDecimal.ZERO;
        BigDecimal result = price.subtract(shipping).subtract(refund);
        BigDecimal bigDecimal = this.commissionableAmount = result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
        if (this.commissionRate != null && this.commissionableAmount != null) {
            this.commissionAmount = this.commissionableAmount.multiply(this.commissionRate).divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
        }
    }

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

    public String getDiscountCode() {
        return this.discountCode;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public BigDecimal getTotalShipping() {
        return this.totalShipping;
    }

    public BigDecimal getTotalRefund() {
        return this.totalRefund;
    }

    public BigDecimal getCommissionableAmount() {
        return this.commissionableAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public String getCommissionStatus() {
        return this.commissionStatus;
    }

    public String getCommissionSource() {
        return this.commissionSource;
    }

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public LocalDateTime getCancelledAt() {
        return this.cancelledAt;
    }

    public LocalDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public LocalDateTime getInTransitAt() {
        return this.inTransitAt;
    }

    public LocalDateTime getFulfillmentCreatedAt() {
        return this.fulfillmentCreatedAt;
    }

    public LocalDateTime getClosedAt() {
        return this.closedAt;
    }

    public LocalDateTime getEstimatedDeliveryAt() {
        return this.estimatedDeliveryAt;
    }

    public LocalDateTime getProcessedAt() {
        return this.processedAt;
    }

    public LocalDateTime getOrderCreatedAt() {
        return this.orderCreatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getCalculatedAt() {
        return this.calculatedAt;
    }

    public LocalDateTime getPaidAt() {
        return this.paidAt;
    }

    public String getFulfillmentDisplayStatus() {
        return this.fulfillmentDisplayStatus;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
    }

    public String getTrackingCompany() {
        return this.trackingCompany;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public String getTrackingUrl() {
        return this.trackingUrl;
    }

    public String getTrackingEventsJson() {
        return this.trackingEventsJson;
    }

    public String getRecipientName() {
        return this.recipientName;
    }

    public String getRecipientPhone() {
        return this.recipientPhone;
    }

    public String getRecipientAddress() {
        return this.recipientAddress;
    }

    public String getRecipientCountry() {
        return this.recipientCountry;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
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

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTotalShipping(BigDecimal totalShipping) {
        this.totalShipping = totalShipping;
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }

    public void setCommissionableAmount(BigDecimal commissionableAmount) {
        this.commissionableAmount = commissionableAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setCommissionStatus(String commissionStatus) {
        this.commissionStatus = commissionStatus;
    }

    public void setCommissionSource(String commissionSource) {
        this.commissionSource = commissionSource;
    }

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setInTransitAt(LocalDateTime inTransitAt) {
        this.inTransitAt = inTransitAt;
    }

    public void setFulfillmentCreatedAt(LocalDateTime fulfillmentCreatedAt) {
        this.fulfillmentCreatedAt = fulfillmentCreatedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setEstimatedDeliveryAt(LocalDateTime estimatedDeliveryAt) {
        this.estimatedDeliveryAt = estimatedDeliveryAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setOrderCreatedAt(LocalDateTime orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setFulfillmentDisplayStatus(String fulfillmentDisplayStatus) {
        this.fulfillmentDisplayStatus = fulfillmentDisplayStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setTrackingCompany(String trackingCompany) {
        this.trackingCompany = trackingCompany;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public void setTrackingEventsJson(String trackingEventsJson) {
        this.trackingEventsJson = trackingEventsJson;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public void setRecipientCountry(String recipientCountry) {
        this.recipientCountry = recipientCountry;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerConversionOrder)) {
            return false;
        }
        InfluencerConversionOrder other = (InfluencerConversionOrder)o;
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
        String this$discountCode = this.getDiscountCode();
        String other$discountCode = other.getDiscountCode();
        if (this$discountCode == null ? other$discountCode != null : !this$discountCode.equals(other$discountCode)) {
            return false;
        }
        BigDecimal this$totalPrice = this.getTotalPrice();
        BigDecimal other$totalPrice = other.getTotalPrice();
        if (this$totalPrice == null ? other$totalPrice != null : !((Object)this$totalPrice).equals(other$totalPrice)) {
            return false;
        }
        BigDecimal this$totalShipping = this.getTotalShipping();
        BigDecimal other$totalShipping = other.getTotalShipping();
        if (this$totalShipping == null ? other$totalShipping != null : !((Object)this$totalShipping).equals(other$totalShipping)) {
            return false;
        }
        BigDecimal this$totalRefund = this.getTotalRefund();
        BigDecimal other$totalRefund = other.getTotalRefund();
        if (this$totalRefund == null ? other$totalRefund != null : !((Object)this$totalRefund).equals(other$totalRefund)) {
            return false;
        }
        BigDecimal this$commissionableAmount = this.getCommissionableAmount();
        BigDecimal other$commissionableAmount = other.getCommissionableAmount();
        if (this$commissionableAmount == null ? other$commissionableAmount != null : !((Object)this$commissionableAmount).equals(other$commissionableAmount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
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
        String this$commissionStatus = this.getCommissionStatus();
        String other$commissionStatus = other.getCommissionStatus();
        if (this$commissionStatus == null ? other$commissionStatus != null : !this$commissionStatus.equals(other$commissionStatus)) {
            return false;
        }
        String this$commissionSource = this.getCommissionSource();
        String other$commissionSource = other.getCommissionSource();
        if (this$commissionSource == null ? other$commissionSource != null : !this$commissionSource.equals(other$commissionSource)) {
            return false;
        }
        String this$financialStatus = this.getFinancialStatus();
        String other$financialStatus = other.getFinancialStatus();
        if (this$financialStatus == null ? other$financialStatus != null : !this$financialStatus.equals(other$financialStatus)) {
            return false;
        }
        LocalDateTime this$cancelledAt = this.getCancelledAt();
        LocalDateTime other$cancelledAt = other.getCancelledAt();
        if (this$cancelledAt == null ? other$cancelledAt != null : !((Object)this$cancelledAt).equals(other$cancelledAt)) {
            return false;
        }
        LocalDateTime this$deliveredAt = this.getDeliveredAt();
        LocalDateTime other$deliveredAt = other.getDeliveredAt();
        if (this$deliveredAt == null ? other$deliveredAt != null : !((Object)this$deliveredAt).equals(other$deliveredAt)) {
            return false;
        }
        LocalDateTime this$inTransitAt = this.getInTransitAt();
        LocalDateTime other$inTransitAt = other.getInTransitAt();
        if (this$inTransitAt == null ? other$inTransitAt != null : !((Object)this$inTransitAt).equals(other$inTransitAt)) {
            return false;
        }
        LocalDateTime this$fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        LocalDateTime other$fulfillmentCreatedAt = other.getFulfillmentCreatedAt();
        if (this$fulfillmentCreatedAt == null ? other$fulfillmentCreatedAt != null : !((Object)this$fulfillmentCreatedAt).equals(other$fulfillmentCreatedAt)) {
            return false;
        }
        LocalDateTime this$closedAt = this.getClosedAt();
        LocalDateTime other$closedAt = other.getClosedAt();
        if (this$closedAt == null ? other$closedAt != null : !((Object)this$closedAt).equals(other$closedAt)) {
            return false;
        }
        LocalDateTime this$estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        LocalDateTime other$estimatedDeliveryAt = other.getEstimatedDeliveryAt();
        if (this$estimatedDeliveryAt == null ? other$estimatedDeliveryAt != null : !((Object)this$estimatedDeliveryAt).equals(other$estimatedDeliveryAt)) {
            return false;
        }
        LocalDateTime this$processedAt = this.getProcessedAt();
        LocalDateTime other$processedAt = other.getProcessedAt();
        if (this$processedAt == null ? other$processedAt != null : !((Object)this$processedAt).equals(other$processedAt)) {
            return false;
        }
        LocalDateTime this$orderCreatedAt = this.getOrderCreatedAt();
        LocalDateTime other$orderCreatedAt = other.getOrderCreatedAt();
        if (this$orderCreatedAt == null ? other$orderCreatedAt != null : !((Object)this$orderCreatedAt).equals(other$orderCreatedAt)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
            return false;
        }
        LocalDateTime this$calculatedAt = this.getCalculatedAt();
        LocalDateTime other$calculatedAt = other.getCalculatedAt();
        if (this$calculatedAt == null ? other$calculatedAt != null : !((Object)this$calculatedAt).equals(other$calculatedAt)) {
            return false;
        }
        LocalDateTime this$paidAt = this.getPaidAt();
        LocalDateTime other$paidAt = other.getPaidAt();
        if (this$paidAt == null ? other$paidAt != null : !((Object)this$paidAt).equals(other$paidAt)) {
            return false;
        }
        String this$fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        String other$fulfillmentDisplayStatus = other.getFulfillmentDisplayStatus();
        if (this$fulfillmentDisplayStatus == null ? other$fulfillmentDisplayStatus != null : !this$fulfillmentDisplayStatus.equals(other$fulfillmentDisplayStatus)) {
            return false;
        }
        String this$fulfillmentStatus = this.getFulfillmentStatus();
        String other$fulfillmentStatus = other.getFulfillmentStatus();
        if (this$fulfillmentStatus == null ? other$fulfillmentStatus != null : !this$fulfillmentStatus.equals(other$fulfillmentStatus)) {
            return false;
        }
        String this$trackingCompany = this.getTrackingCompany();
        String other$trackingCompany = other.getTrackingCompany();
        if (this$trackingCompany == null ? other$trackingCompany != null : !this$trackingCompany.equals(other$trackingCompany)) {
            return false;
        }
        String this$trackingNumber = this.getTrackingNumber();
        String other$trackingNumber = other.getTrackingNumber();
        if (this$trackingNumber == null ? other$trackingNumber != null : !this$trackingNumber.equals(other$trackingNumber)) {
            return false;
        }
        String this$trackingUrl = this.getTrackingUrl();
        String other$trackingUrl = other.getTrackingUrl();
        if (this$trackingUrl == null ? other$trackingUrl != null : !this$trackingUrl.equals(other$trackingUrl)) {
            return false;
        }
        String this$trackingEventsJson = this.getTrackingEventsJson();
        String other$trackingEventsJson = other.getTrackingEventsJson();
        if (this$trackingEventsJson == null ? other$trackingEventsJson != null : !this$trackingEventsJson.equals(other$trackingEventsJson)) {
            return false;
        }
        String this$recipientName = this.getRecipientName();
        String other$recipientName = other.getRecipientName();
        if (this$recipientName == null ? other$recipientName != null : !this$recipientName.equals(other$recipientName)) {
            return false;
        }
        String this$recipientPhone = this.getRecipientPhone();
        String other$recipientPhone = other.getRecipientPhone();
        if (this$recipientPhone == null ? other$recipientPhone != null : !this$recipientPhone.equals(other$recipientPhone)) {
            return false;
        }
        String this$recipientAddress = this.getRecipientAddress();
        String other$recipientAddress = other.getRecipientAddress();
        if (this$recipientAddress == null ? other$recipientAddress != null : !this$recipientAddress.equals(other$recipientAddress)) {
            return false;
        }
        String this$recipientCountry = this.getRecipientCountry();
        String other$recipientCountry = other.getRecipientCountry();
        if (this$recipientCountry == null ? other$recipientCountry != null : !this$recipientCountry.equals(other$recipientCountry)) {
            return false;
        }
        String this$customerEmail = this.getCustomerEmail();
        String other$customerEmail = other.getCustomerEmail();
        return !(this$customerEmail == null ? other$customerEmail != null : !this$customerEmail.equals(other$customerEmail));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerConversionOrder;
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
        String $discountCode = this.getDiscountCode();
        result = result * 59 + ($discountCode == null ? 43 : $discountCode.hashCode());
        BigDecimal $totalPrice = this.getTotalPrice();
        result = result * 59 + ($totalPrice == null ? 43 : ((Object)$totalPrice).hashCode());
        BigDecimal $totalShipping = this.getTotalShipping();
        result = result * 59 + ($totalShipping == null ? 43 : ((Object)$totalShipping).hashCode());
        BigDecimal $totalRefund = this.getTotalRefund();
        result = result * 59 + ($totalRefund == null ? 43 : ((Object)$totalRefund).hashCode());
        BigDecimal $commissionableAmount = this.getCommissionableAmount();
        result = result * 59 + ($commissionableAmount == null ? 43 : ((Object)$commissionableAmount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        BigDecimal $commissionRate = this.getCommissionRate();
        result = result * 59 + ($commissionRate == null ? 43 : ((Object)$commissionRate).hashCode());
        BigDecimal $commissionAmount = this.getCommissionAmount();
        result = result * 59 + ($commissionAmount == null ? 43 : ((Object)$commissionAmount).hashCode());
        String $commissionStatus = this.getCommissionStatus();
        result = result * 59 + ($commissionStatus == null ? 43 : $commissionStatus.hashCode());
        String $commissionSource = this.getCommissionSource();
        result = result * 59 + ($commissionSource == null ? 43 : $commissionSource.hashCode());
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        LocalDateTime $cancelledAt = this.getCancelledAt();
        result = result * 59 + ($cancelledAt == null ? 43 : ((Object)$cancelledAt).hashCode());
        LocalDateTime $deliveredAt = this.getDeliveredAt();
        result = result * 59 + ($deliveredAt == null ? 43 : ((Object)$deliveredAt).hashCode());
        LocalDateTime $inTransitAt = this.getInTransitAt();
        result = result * 59 + ($inTransitAt == null ? 43 : ((Object)$inTransitAt).hashCode());
        LocalDateTime $fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        result = result * 59 + ($fulfillmentCreatedAt == null ? 43 : ((Object)$fulfillmentCreatedAt).hashCode());
        LocalDateTime $closedAt = this.getClosedAt();
        result = result * 59 + ($closedAt == null ? 43 : ((Object)$closedAt).hashCode());
        LocalDateTime $estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        result = result * 59 + ($estimatedDeliveryAt == null ? 43 : ((Object)$estimatedDeliveryAt).hashCode());
        LocalDateTime $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : ((Object)$processedAt).hashCode());
        LocalDateTime $orderCreatedAt = this.getOrderCreatedAt();
        result = result * 59 + ($orderCreatedAt == null ? 43 : ((Object)$orderCreatedAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        LocalDateTime $calculatedAt = this.getCalculatedAt();
        result = result * 59 + ($calculatedAt == null ? 43 : ((Object)$calculatedAt).hashCode());
        LocalDateTime $paidAt = this.getPaidAt();
        result = result * 59 + ($paidAt == null ? 43 : ((Object)$paidAt).hashCode());
        String $fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        result = result * 59 + ($fulfillmentDisplayStatus == null ? 43 : $fulfillmentDisplayStatus.hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        String $trackingCompany = this.getTrackingCompany();
        result = result * 59 + ($trackingCompany == null ? 43 : $trackingCompany.hashCode());
        String $trackingNumber = this.getTrackingNumber();
        result = result * 59 + ($trackingNumber == null ? 43 : $trackingNumber.hashCode());
        String $trackingUrl = this.getTrackingUrl();
        result = result * 59 + ($trackingUrl == null ? 43 : $trackingUrl.hashCode());
        String $trackingEventsJson = this.getTrackingEventsJson();
        result = result * 59 + ($trackingEventsJson == null ? 43 : $trackingEventsJson.hashCode());
        String $recipientName = this.getRecipientName();
        result = result * 59 + ($recipientName == null ? 43 : $recipientName.hashCode());
        String $recipientPhone = this.getRecipientPhone();
        result = result * 59 + ($recipientPhone == null ? 43 : $recipientPhone.hashCode());
        String $recipientAddress = this.getRecipientAddress();
        result = result * 59 + ($recipientAddress == null ? 43 : $recipientAddress.hashCode());
        String $recipientCountry = this.getRecipientCountry();
        result = result * 59 + ($recipientCountry == null ? 43 : $recipientCountry.hashCode());
        String $customerEmail = this.getCustomerEmail();
        result = result * 59 + ($customerEmail == null ? 43 : $customerEmail.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerConversionOrder(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber() + ", orderName=" + this.getOrderName() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", discountCode=" + this.getDiscountCode() + ", totalPrice=" + this.getTotalPrice() + ", totalShipping=" + this.getTotalShipping() + ", totalRefund=" + this.getTotalRefund() + ", commissionableAmount=" + this.getCommissionableAmount() + ", currency=" + this.getCurrency() + ", commissionRate=" + this.getCommissionRate() + ", commissionAmount=" + this.getCommissionAmount() + ", commissionStatus=" + this.getCommissionStatus() + ", commissionSource=" + this.getCommissionSource() + ", financialStatus=" + this.getFinancialStatus() + ", cancelledAt=" + this.getCancelledAt() + ", deliveredAt=" + this.getDeliveredAt() + ", inTransitAt=" + this.getInTransitAt() + ", fulfillmentCreatedAt=" + this.getFulfillmentCreatedAt() + ", closedAt=" + this.getClosedAt() + ", estimatedDeliveryAt=" + this.getEstimatedDeliveryAt() + ", processedAt=" + this.getProcessedAt() + ", orderCreatedAt=" + this.getOrderCreatedAt() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", calculatedAt=" + this.getCalculatedAt() + ", paidAt=" + this.getPaidAt() + ", fulfillmentDisplayStatus=" + this.getFulfillmentDisplayStatus() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", trackingCompany=" + this.getTrackingCompany() + ", trackingNumber=" + this.getTrackingNumber() + ", trackingUrl=" + this.getTrackingUrl() + ", trackingEventsJson=" + this.getTrackingEventsJson() + ", recipientName=" + this.getRecipientName() + ", recipientPhone=" + this.getRecipientPhone() + ", recipientAddress=" + this.getRecipientAddress() + ", recipientCountry=" + this.getRecipientCountry() + ", customerEmail=" + this.getCustomerEmail() + ")";
    }
}

