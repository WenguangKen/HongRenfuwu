package com.athlunakms.eccang.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="influencer_sample_order")
public class InfluencerSampleOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id", nullable=false, unique=true)
    private Long orderId;
    @Column(name="eccang_order_id", length=100)
    private String eccangOrderId;
    @Column(name="eccang_order_number")
    private Long eccangOrderNumber;
    @Column(name="order_name", length=50)
    private String orderName;
    @Column(name="influencer_id")
    private Long influencerId;
    @Column(name="influencer_name", length=200)
    private String influencerName;
    @Column(name="total_price", precision=12, scale=2)
    private BigDecimal totalPrice;
    @Column(length=10)
    private String currency = "USD";
    @Column(name="sample_reason", length=500)
    private String sampleReason;
    @Column(name="order_created_at")
    private LocalDateTime orderCreatedAt;
    @Column(name="financial_status", length=50)
    private String financialStatus;
    @Column(name="cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name="fulfillment_display_status", length=100)
    private String fulfillmentDisplayStatus;
    @Column(name="fulfillment_status", length=50)
    private String fulfillmentStatus;
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
    @Column(name="auto_matched")
    private Boolean autoMatched = false;
    @Column(name="manual_override")
    private Boolean manualOverride = false;
    @Column(name="customer_email")
    private String customerEmail;
    @Column(name="processed_at")
    private LocalDateTime processedAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="is_draft")
    private Boolean isDraft = false;

    @Column(name="cooperation_price", precision=12, scale=2)
    private BigDecimal cooperationPrice;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getEccangOrderId() {
        return this.eccangOrderId;
    }

    public Long getEccangOrderNumber() {
        return this.eccangOrderNumber;
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

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public LocalDateTime getCancelledAt() {
        return this.cancelledAt;
    }

    public String getFulfillmentDisplayStatus() {
        return this.fulfillmentDisplayStatus;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
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

    public Boolean getAutoMatched() {
        return this.autoMatched;
    }

    public Boolean getManualOverride() {
        return this.manualOverride;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public LocalDateTime getProcessedAt() {
        return this.processedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Boolean getIsDraft() {
        return this.isDraft;
    }

    public BigDecimal getCooperationPrice() {
        return this.cooperationPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setEccangOrderId(String eccangOrderId) {
        this.eccangOrderId = eccangOrderId;
    }

    public void setEccangOrderNumber(Long eccangOrderNumber) {
        this.eccangOrderNumber = eccangOrderNumber;
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

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setFulfillmentDisplayStatus(String fulfillmentDisplayStatus) {
        this.fulfillmentDisplayStatus = fulfillmentDisplayStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
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

    public void setAutoMatched(Boolean autoMatched) {
        this.autoMatched = autoMatched;
    }

    public void setManualOverride(Boolean manualOverride) {
        this.manualOverride = manualOverride;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public void setCooperationPrice(BigDecimal cooperationPrice) {
        this.cooperationPrice = cooperationPrice;
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
        Long this$eccangOrderNumber = this.getEccangOrderNumber();
        Long other$eccangOrderNumber = other.getEccangOrderNumber();
        if (this$eccangOrderNumber == null ? other$eccangOrderNumber != null : !((Object)this$eccangOrderNumber).equals(other$eccangOrderNumber)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Boolean this$autoMatched = this.getAutoMatched();
        Boolean other$autoMatched = other.getAutoMatched();
        if (this$autoMatched == null ? other$autoMatched != null : !((Object)this$autoMatched).equals(other$autoMatched)) {
            return false;
        }
        Boolean this$manualOverride = this.getManualOverride();
        Boolean other$manualOverride = other.getManualOverride();
        if (this$manualOverride == null ? other$manualOverride != null : !((Object)this$manualOverride).equals(other$manualOverride)) {
            return false;
        }
        Boolean this$isDraft = this.getIsDraft();
        Boolean other$isDraft = other.getIsDraft();
        if (this$isDraft == null ? other$isDraft != null : !((Object)this$isDraft).equals(other$isDraft)) {
            return false;
        }
        String this$eccangOrderId = this.getEccangOrderId();
        String other$eccangOrderId = other.getEccangOrderId();
        if (this$eccangOrderId == null ? other$eccangOrderId != null : !this$eccangOrderId.equals(other$eccangOrderId)) {
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
        if (this$customerEmail == null ? other$customerEmail != null : !this$customerEmail.equals(other$customerEmail)) {
            return false;
        }
        LocalDateTime this$processedAt = this.getProcessedAt();
        LocalDateTime other$processedAt = other.getProcessedAt();
        if (this$processedAt == null ? other$processedAt != null : !((Object)this$processedAt).equals(other$processedAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
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
        Long $eccangOrderNumber = this.getEccangOrderNumber();
        result = result * 59 + ($eccangOrderNumber == null ? 43 : ((Object)$eccangOrderNumber).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Boolean $autoMatched = this.getAutoMatched();
        result = result * 59 + ($autoMatched == null ? 43 : ((Object)$autoMatched).hashCode());
        Boolean $manualOverride = this.getManualOverride();
        result = result * 59 + ($manualOverride == null ? 43 : ((Object)$manualOverride).hashCode());
        Boolean $isDraft = this.getIsDraft();
        result = result * 59 + ($isDraft == null ? 43 : ((Object)$isDraft).hashCode());
        String $eccangOrderId = this.getEccangOrderId();
        result = result * 59 + ($eccangOrderId == null ? 43 : $eccangOrderId.hashCode());
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
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        LocalDateTime $cancelledAt = this.getCancelledAt();
        result = result * 59 + ($cancelledAt == null ? 43 : ((Object)$cancelledAt).hashCode());
        String $fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        result = result * 59 + ($fulfillmentDisplayStatus == null ? 43 : $fulfillmentDisplayStatus.hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
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
        LocalDateTime $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : ((Object)$processedAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerSampleOrder(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", eccangOrderId=" + this.getEccangOrderId() + ", eccangOrderNumber=" + this.getEccangOrderNumber() + ", orderName=" + this.getOrderName() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", totalPrice=" + this.getTotalPrice() + ", currency=" + this.getCurrency() + ", sampleReason=" + this.getSampleReason() + ", orderCreatedAt=" + this.getOrderCreatedAt() + ", financialStatus=" + this.getFinancialStatus() + ", cancelledAt=" + this.getCancelledAt() + ", fulfillmentDisplayStatus=" + this.getFulfillmentDisplayStatus() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", deliveredAt=" + this.getDeliveredAt() + ", inTransitAt=" + this.getInTransitAt() + ", fulfillmentCreatedAt=" + this.getFulfillmentCreatedAt() + ", closedAt=" + this.getClosedAt() + ", estimatedDeliveryAt=" + this.getEstimatedDeliveryAt() + ", trackingCompany=" + this.getTrackingCompany() + ", trackingNumber=" + this.getTrackingNumber() + ", trackingUrl=" + this.getTrackingUrl() + ", trackingEventsJson=" + this.getTrackingEventsJson() + ", recipientName=" + this.getRecipientName() + ", recipientPhone=" + this.getRecipientPhone() + ", recipientAddress=" + this.getRecipientAddress() + ", recipientCountry=" + this.getRecipientCountry() + ", autoMatched=" + this.getAutoMatched() + ", manualOverride=" + this.getManualOverride() + ", customerEmail=" + this.getCustomerEmail() + ", processedAt=" + this.getProcessedAt() + ", updatedAt=" + this.getUpdatedAt() + ", createdAt=" + this.getCreatedAt() + ", isDraft=" + this.getIsDraft() + ")";
    }
}

