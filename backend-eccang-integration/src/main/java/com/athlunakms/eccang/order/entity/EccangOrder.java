package com.athlunakms.eccang.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="orders")
public class EccangOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="store_id", nullable=false)
    private Long storeId;
    @Column(name="store_name", length=200)
    private String storeName;
    @Column(name="shop_domain", length=255)
    private String shopDomain;
    @Column(name="eccang_order_id", nullable=false, unique=true, length=100)
    private String eccangOrderId;
    @Column(name="eccang_order_number")
    private Long eccangOrderNumber;
    @Column(nullable=false, length=50)
    private String name;
    @Column(name="order_date")
    private LocalDateTime createdAtEccang;
    @Column(name="processed_at_eccang")
    private LocalDateTime processedAtEccang;
    @Column(name="updated_at_eccang")
    private LocalDateTime updatedAtEccang;
    @Column(name="closed_at")
    private LocalDateTime closedAtEccang;
    @Column(name="cancelled_at")
    private LocalDateTime cancelledAtEccang;
    @Column(name="financial_status", length=50)
    private String financialStatus;
    @Column(name="fulfillment_status", length=50)
    private String fulfillmentStatus;
    @Column(name="cancel_reason", length=255)
    private String cancelReason;
    @Column(name="fully_paid")
    private Boolean fullyPaid = false;
    @Column(length=10)
    private String currency = "USD";
    @Column(name="subtotal", precision=12, scale=2)
    private BigDecimal subtotalPrice;
    @Column(name="total_tax", precision=12, scale=2)
    private BigDecimal totalTax;
    @Column(name="total_discount", precision=12, scale=2)
    private BigDecimal totalDiscounts;
    @Column(name="total_shipping_price", precision=12, scale=2)
    private BigDecimal totalShippingPrice;
    @Column(name="total_price", precision=12, scale=2)
    private BigDecimal totalPrice;
    @Column(name="refund_total", precision=12, scale=2)
    private BigDecimal refundTotal;
    @Column(name="customer_id")
    private Long customerId;
    @Column(name="eccang_customer_id", length=100)
    private String eccangCustomerId;
    @Column(name="customer_email", length=255)
    private String customerEmail;
    @Column(name="customer_first_name", length=100)
    private String customerFirstName;
    @Column(name="customer_last_name", length=100)
    private String customerLastName;
    @Column(name="customer_phone", length=50)
    private String customerPhone;
    @Column(name="shipping_name", length=200)
    private String shippingName;
    @Column(name="shipping_phone", length=50)
    private String shippingPhone;
    @Column(name="shipping_address1", length=500)
    private String shippingAddress1;
    @Column(name="shipping_address2", length=500)
    private String shippingAddress2;
    @Column(name="shipping_city", length=100)
    private String shippingCity;
    @Column(name="shipping_province", length=100)
    private String shippingProvince;
    @Column(name="shipping_province_code", length=20)
    private String shippingProvinceCode;
    @Column(name="shipping_country", length=100)
    private String shippingCountry;
    @Column(name="shipping_country_code", length=10)
    private String shippingCountryCode;
    @Column(name="shipping_zip", length=20)
    private String shippingZip;
    @Column(name="billing_name", length=200)
    private String billingName;
    @Column(name="billing_phone", length=50)
    private String billingPhone;
    @Column(name="billing_address1", length=500)
    private String billingAddress1;
    @Column(name="billing_address2", length=500)
    private String billingAddress2;
    @Column(name="billing_city", length=100)
    private String billingCity;
    @Column(name="billing_province", length=100)
    private String billingProvince;
    @Column(name="billing_province_code", length=20)
    private String billingProvinceCode;
    @Column(name="billing_country", length=100)
    private String billingCountry;
    @Column(name="billing_country_code", length=10)
    private String billingCountryCode;
    @Column(name="billing_zip", length=20)
    private String billingZip;
    @Column(name="source_name", length=100)
    private String sourceName;
    @Column(name="landing_site", columnDefinition="TEXT")
    private String landingSite;
    @Column(name="referring_site", columnDefinition="TEXT")
    private String referringSite;
    @Column(name="shipping_method", length=255)
    private String shippingMethod;
    @Column(name="discount_codes", length=500)
    private String discountCodes;
    @Column(name="discount_codes_json", columnDefinition="JSON")
    private String discountCodesJson;
    @Column(length=1000)
    private String tags;
    @Column(columnDefinition="TEXT")
    private String note;
    @Column(name="order_source", length=50)
    private String orderSource;
    @Column(name="creation_mode", length=50)
    private String creationMode;
    @Column(name="influencer_id")
    private Long influencerId;
    @Column(name="influencer_name", length=200)
    private String influencerName;
    @Column(name="influencer_discount_code", length=100)
    private String influencerDiscountCode;
    @Column(name="commission_status", length=50)
    private String commissionStatus = "pending";
    @Column(name="commission_amount", precision=12, scale=2)
    private BigDecimal commissionAmount;
    @Column(name="commission_rate", precision=5, scale=2)
    private BigDecimal commissionRate;
    @Column(name="sync_status", length=50)
    private String syncStatus = "synced";
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name="synced_at")
    private LocalDateTime syncAt;
    @Column(name="tracking_numbers", length=500)
    private String trackingNumbers;
    @Column(name="fulfillment_ids", length=500)
    private String fulfillmentIds;
    @Column(name="fulfillments_json", columnDefinition="JSON")
    private String fulfillmentsJson;
    @Column(name="fulfillment_display_status", length=100)
    private String fulfillmentDisplayStatus;
    @Column(name="delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name="fulfillment_created_at")
    private LocalDateTime fulfillmentCreatedAt;
    @Column(name="in_transit_at")
    private LocalDateTime inTransitAt;
    @Column(name="estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;
    @Column(name="is_draft")
    private Boolean isDraft = false;
    @Column(name="is_fba_shipment", nullable=false)
    private Boolean isFbaShipment = false;
    @Column(name="fba_warehouse_code", length=50)
    private String fbaWarehouseCode;
    @Column(name="fba_shipping_method", length=50)
    private String fbaShippingMethod;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.syncAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.syncAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public String getShopDomain() {
        return this.shopDomain;
    }

    public String getEccangOrderId() {
        return this.eccangOrderId;
    }

    public Long getEccangOrderNumber() {
        return this.eccangOrderNumber;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getCreatedAtEccang() {
        return this.createdAtEccang;
    }

    public LocalDateTime getProcessedAtEccang() {
        return this.processedAtEccang;
    }

    public LocalDateTime getUpdatedAtEccang() {
        return this.updatedAtEccang;
    }

    public LocalDateTime getClosedAtEccang() {
        return this.closedAtEccang;
    }

    public LocalDateTime getCancelledAtEccang() {
        return this.cancelledAtEccang;
    }

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
    }

    public String getCancelReason() {
        return this.cancelReason;
    }

    public Boolean getFullyPaid() {
        return this.fullyPaid;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getSubtotalPrice() {
        return this.subtotalPrice;
    }

    public BigDecimal getTotalTax() {
        return this.totalTax;
    }

    public BigDecimal getTotalDiscounts() {
        return this.totalDiscounts;
    }

    public BigDecimal getTotalShippingPrice() {
        return this.totalShippingPrice;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public BigDecimal getRefundTotal() {
        return this.refundTotal;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public String getEccangCustomerId() {
        return this.eccangCustomerId;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public String getCustomerFirstName() {
        return this.customerFirstName;
    }

    public String getCustomerLastName() {
        return this.customerLastName;
    }

    public String getCustomerPhone() {
        return this.customerPhone;
    }

    public String getShippingName() {
        return this.shippingName;
    }

    public String getShippingPhone() {
        return this.shippingPhone;
    }

    public String getShippingAddress1() {
        return this.shippingAddress1;
    }

    public String getShippingAddress2() {
        return this.shippingAddress2;
    }

    public String getShippingCity() {
        return this.shippingCity;
    }

    public String getShippingProvince() {
        return this.shippingProvince;
    }

    public String getShippingProvinceCode() {
        return this.shippingProvinceCode;
    }

    public String getShippingCountry() {
        return this.shippingCountry;
    }

    public String getShippingCountryCode() {
        return this.shippingCountryCode;
    }

    public String getShippingZip() {
        return this.shippingZip;
    }

    public String getBillingName() {
        return this.billingName;
    }

    public String getBillingPhone() {
        return this.billingPhone;
    }

    public String getBillingAddress1() {
        return this.billingAddress1;
    }

    public String getBillingAddress2() {
        return this.billingAddress2;
    }

    public String getBillingCity() {
        return this.billingCity;
    }

    public String getBillingProvince() {
        return this.billingProvince;
    }

    public String getBillingProvinceCode() {
        return this.billingProvinceCode;
    }

    public String getBillingCountry() {
        return this.billingCountry;
    }

    public String getBillingCountryCode() {
        return this.billingCountryCode;
    }

    public String getBillingZip() {
        return this.billingZip;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public String getLandingSite() {
        return this.landingSite;
    }

    public String getReferringSite() {
        return this.referringSite;
    }

    public String getShippingMethod() {
        return this.shippingMethod;
    }

    public String getDiscountCodes() {
        return this.discountCodes;
    }

    public String getDiscountCodesJson() {
        return this.discountCodesJson;
    }

    public String getTags() {
        return this.tags;
    }

    public String getNote() {
        return this.note;
    }

    public String getOrderSource() {
        return this.orderSource;
    }

    public String getCreationMode() {
        return this.creationMode;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public String getInfluencerDiscountCode() {
        return this.influencerDiscountCode;
    }

    public String getCommissionStatus() {
        return this.commissionStatus;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }

    public String getSyncStatus() {
        return this.syncStatus;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getSyncAt() {
        return this.syncAt;
    }

    public String getTrackingNumbers() {
        return this.trackingNumbers;
    }

    public String getFulfillmentIds() {
        return this.fulfillmentIds;
    }

    public String getFulfillmentsJson() {
        return this.fulfillmentsJson;
    }

    public String getFulfillmentDisplayStatus() {
        return this.fulfillmentDisplayStatus;
    }

    public LocalDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public LocalDateTime getFulfillmentCreatedAt() {
        return this.fulfillmentCreatedAt;
    }

    public LocalDateTime getInTransitAt() {
        return this.inTransitAt;
    }

    public LocalDateTime getEstimatedDeliveryAt() {
        return this.estimatedDeliveryAt;
    }

    public Boolean getIsDraft() {
        return this.isDraft;
    }

    public Boolean getIsFbaShipment() {
        return this.isFbaShipment;
    }

    public String getFbaWarehouseCode() {
        return this.fbaWarehouseCode;
    }

    public String getFbaShippingMethod() {
        return this.fbaShippingMethod;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain;
    }

    public void setEccangOrderId(String eccangOrderId) {
        this.eccangOrderId = eccangOrderId;
    }

    public void setEccangOrderNumber(Long eccangOrderNumber) {
        this.eccangOrderNumber = eccangOrderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAtEccang(LocalDateTime createdAtEccang) {
        this.createdAtEccang = createdAtEccang;
    }

    public void setProcessedAtEccang(LocalDateTime processedAtEccang) {
        this.processedAtEccang = processedAtEccang;
    }

    public void setUpdatedAtEccang(LocalDateTime updatedAtEccang) {
        this.updatedAtEccang = updatedAtEccang;
    }

    public void setClosedAtEccang(LocalDateTime closedAtEccang) {
        this.closedAtEccang = closedAtEccang;
    }

    public void setCancelledAtEccang(LocalDateTime cancelledAtEccang) {
        this.cancelledAtEccang = cancelledAtEccang;
    }

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void setFullyPaid(Boolean fullyPaid) {
        this.fullyPaid = fullyPaid;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setSubtotalPrice(BigDecimal subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public void setTotalDiscounts(BigDecimal totalDiscounts) {
        this.totalDiscounts = totalDiscounts;
    }

    public void setTotalShippingPrice(BigDecimal totalShippingPrice) {
        this.totalShippingPrice = totalShippingPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setRefundTotal(BigDecimal refundTotal) {
        this.refundTotal = refundTotal;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setEccangCustomerId(String eccangCustomerId) {
        this.eccangCustomerId = eccangCustomerId;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }

    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public void setShippingProvince(String shippingProvince) {
        this.shippingProvince = shippingProvince;
    }

    public void setShippingProvinceCode(String shippingProvinceCode) {
        this.shippingProvinceCode = shippingProvinceCode;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public void setShippingCountryCode(String shippingCountryCode) {
        this.shippingCountryCode = shippingCountryCode;
    }

    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }

    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    public void setBillingPhone(String billingPhone) {
        this.billingPhone = billingPhone;
    }

    public void setBillingAddress1(String billingAddress1) {
        this.billingAddress1 = billingAddress1;
    }

    public void setBillingAddress2(String billingAddress2) {
        this.billingAddress2 = billingAddress2;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public void setBillingProvince(String billingProvince) {
        this.billingProvince = billingProvince;
    }

    public void setBillingProvinceCode(String billingProvinceCode) {
        this.billingProvinceCode = billingProvinceCode;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }

    public void setBillingCountryCode(String billingCountryCode) {
        this.billingCountryCode = billingCountryCode;
    }

    public void setBillingZip(String billingZip) {
        this.billingZip = billingZip;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setLandingSite(String landingSite) {
        this.landingSite = landingSite;
    }

    public void setReferringSite(String referringSite) {
        this.referringSite = referringSite;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public void setDiscountCodes(String discountCodes) {
        this.discountCodes = discountCodes;
    }

    public void setDiscountCodesJson(String discountCodesJson) {
        this.discountCodesJson = discountCodesJson;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public void setCreationMode(String creationMode) {
        this.creationMode = creationMode;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setInfluencerDiscountCode(String influencerDiscountCode) {
        this.influencerDiscountCode = influencerDiscountCode;
    }

    public void setCommissionStatus(String commissionStatus) {
        this.commissionStatus = commissionStatus;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSyncAt(LocalDateTime syncAt) {
        this.syncAt = syncAt;
    }

    public void setTrackingNumbers(String trackingNumbers) {
        this.trackingNumbers = trackingNumbers;
    }

    public void setFulfillmentIds(String fulfillmentIds) {
        this.fulfillmentIds = fulfillmentIds;
    }

    public void setFulfillmentsJson(String fulfillmentsJson) {
        this.fulfillmentsJson = fulfillmentsJson;
    }

    public void setFulfillmentDisplayStatus(String fulfillmentDisplayStatus) {
        this.fulfillmentDisplayStatus = fulfillmentDisplayStatus;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setFulfillmentCreatedAt(LocalDateTime fulfillmentCreatedAt) {
        this.fulfillmentCreatedAt = fulfillmentCreatedAt;
    }

    public void setInTransitAt(LocalDateTime inTransitAt) {
        this.inTransitAt = inTransitAt;
    }

    public void setEstimatedDeliveryAt(LocalDateTime estimatedDeliveryAt) {
        this.estimatedDeliveryAt = estimatedDeliveryAt;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public void setIsFbaShipment(Boolean isFbaShipment) {
        this.isFbaShipment = isFbaShipment;
    }

    public void setFbaWarehouseCode(String fbaWarehouseCode) {
        this.fbaWarehouseCode = fbaWarehouseCode;
    }

    public void setFbaShippingMethod(String fbaShippingMethod) {
        this.fbaShippingMethod = fbaShippingMethod;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EccangOrder)) {
            return false;
        }
        EccangOrder other = (EccangOrder)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$storeId = this.getStoreId();
        Long other$storeId = other.getStoreId();
        if (this$storeId == null ? other$storeId != null : !((Object)this$storeId).equals(other$storeId)) {
            return false;
        }
        Long this$eccangOrderNumber = this.getEccangOrderNumber();
        Long other$eccangOrderNumber = other.getEccangOrderNumber();
        if (this$eccangOrderNumber == null ? other$eccangOrderNumber != null : !((Object)this$eccangOrderNumber).equals(other$eccangOrderNumber)) {
            return false;
        }
        Boolean this$fullyPaid = this.getFullyPaid();
        Boolean other$fullyPaid = other.getFullyPaid();
        if (this$fullyPaid == null ? other$fullyPaid != null : !((Object)this$fullyPaid).equals(other$fullyPaid)) {
            return false;
        }
        Long this$customerId = this.getCustomerId();
        Long other$customerId = other.getCustomerId();
        if (this$customerId == null ? other$customerId != null : !((Object)this$customerId).equals(other$customerId)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Boolean this$isDraft = this.getIsDraft();
        Boolean other$isDraft = other.getIsDraft();
        if (this$isDraft == null ? other$isDraft != null : !((Object)this$isDraft).equals(other$isDraft)) {
            return false;
        }
        String this$storeName = this.getStoreName();
        String other$storeName = other.getStoreName();
        if (this$storeName == null ? other$storeName != null : !this$storeName.equals(other$storeName)) {
            return false;
        }
        String this$shopDomain = this.getShopDomain();
        String other$shopDomain = other.getShopDomain();
        if (this$shopDomain == null ? other$shopDomain != null : !this$shopDomain.equals(other$shopDomain)) {
            return false;
        }
        String this$eccangOrderId = this.getEccangOrderId();
        String other$eccangOrderId = other.getEccangOrderId();
        if (this$eccangOrderId == null ? other$eccangOrderId != null : !this$eccangOrderId.equals(other$eccangOrderId)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        LocalDateTime this$createdAtEccang = this.getCreatedAtEccang();
        LocalDateTime other$createdAtEccang = other.getCreatedAtEccang();
        if (this$createdAtEccang == null ? other$createdAtEccang != null : !((Object)this$createdAtEccang).equals(other$createdAtEccang)) {
            return false;
        }
        LocalDateTime this$processedAtEccang = this.getProcessedAtEccang();
        LocalDateTime other$processedAtEccang = other.getProcessedAtEccang();
        if (this$processedAtEccang == null ? other$processedAtEccang != null : !((Object)this$processedAtEccang).equals(other$processedAtEccang)) {
            return false;
        }
        LocalDateTime this$updatedAtEccang = this.getUpdatedAtEccang();
        LocalDateTime other$updatedAtEccang = other.getUpdatedAtEccang();
        if (this$updatedAtEccang == null ? other$updatedAtEccang != null : !((Object)this$updatedAtEccang).equals(other$updatedAtEccang)) {
            return false;
        }
        LocalDateTime this$closedAtEccang = this.getClosedAtEccang();
        LocalDateTime other$closedAtEccang = other.getClosedAtEccang();
        if (this$closedAtEccang == null ? other$closedAtEccang != null : !((Object)this$closedAtEccang).equals(other$closedAtEccang)) {
            return false;
        }
        LocalDateTime this$cancelledAtEccang = this.getCancelledAtEccang();
        LocalDateTime other$cancelledAtEccang = other.getCancelledAtEccang();
        if (this$cancelledAtEccang == null ? other$cancelledAtEccang != null : !((Object)this$cancelledAtEccang).equals(other$cancelledAtEccang)) {
            return false;
        }
        String this$financialStatus = this.getFinancialStatus();
        String other$financialStatus = other.getFinancialStatus();
        if (this$financialStatus == null ? other$financialStatus != null : !this$financialStatus.equals(other$financialStatus)) {
            return false;
        }
        String this$fulfillmentStatus = this.getFulfillmentStatus();
        String other$fulfillmentStatus = other.getFulfillmentStatus();
        if (this$fulfillmentStatus == null ? other$fulfillmentStatus != null : !this$fulfillmentStatus.equals(other$fulfillmentStatus)) {
            return false;
        }
        String this$cancelReason = this.getCancelReason();
        String other$cancelReason = other.getCancelReason();
        if (this$cancelReason == null ? other$cancelReason != null : !this$cancelReason.equals(other$cancelReason)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        BigDecimal this$subtotalPrice = this.getSubtotalPrice();
        BigDecimal other$subtotalPrice = other.getSubtotalPrice();
        if (this$subtotalPrice == null ? other$subtotalPrice != null : !((Object)this$subtotalPrice).equals(other$subtotalPrice)) {
            return false;
        }
        BigDecimal this$totalTax = this.getTotalTax();
        BigDecimal other$totalTax = other.getTotalTax();
        if (this$totalTax == null ? other$totalTax != null : !((Object)this$totalTax).equals(other$totalTax)) {
            return false;
        }
        BigDecimal this$totalDiscounts = this.getTotalDiscounts();
        BigDecimal other$totalDiscounts = other.getTotalDiscounts();
        if (this$totalDiscounts == null ? other$totalDiscounts != null : !((Object)this$totalDiscounts).equals(other$totalDiscounts)) {
            return false;
        }
        BigDecimal this$totalShippingPrice = this.getTotalShippingPrice();
        BigDecimal other$totalShippingPrice = other.getTotalShippingPrice();
        if (this$totalShippingPrice == null ? other$totalShippingPrice != null : !((Object)this$totalShippingPrice).equals(other$totalShippingPrice)) {
            return false;
        }
        BigDecimal this$totalPrice = this.getTotalPrice();
        BigDecimal other$totalPrice = other.getTotalPrice();
        if (this$totalPrice == null ? other$totalPrice != null : !((Object)this$totalPrice).equals(other$totalPrice)) {
            return false;
        }
        BigDecimal this$refundTotal = this.getRefundTotal();
        BigDecimal other$refundTotal = other.getRefundTotal();
        if (this$refundTotal == null ? other$refundTotal != null : !((Object)this$refundTotal).equals(other$refundTotal)) {
            return false;
        }
        String this$eccangCustomerId = this.getEccangCustomerId();
        String other$eccangCustomerId = other.getEccangCustomerId();
        if (this$eccangCustomerId == null ? other$eccangCustomerId != null : !this$eccangCustomerId.equals(other$eccangCustomerId)) {
            return false;
        }
        String this$customerEmail = this.getCustomerEmail();
        String other$customerEmail = other.getCustomerEmail();
        if (this$customerEmail == null ? other$customerEmail != null : !this$customerEmail.equals(other$customerEmail)) {
            return false;
        }
        String this$customerFirstName = this.getCustomerFirstName();
        String other$customerFirstName = other.getCustomerFirstName();
        if (this$customerFirstName == null ? other$customerFirstName != null : !this$customerFirstName.equals(other$customerFirstName)) {
            return false;
        }
        String this$customerLastName = this.getCustomerLastName();
        String other$customerLastName = other.getCustomerLastName();
        if (this$customerLastName == null ? other$customerLastName != null : !this$customerLastName.equals(other$customerLastName)) {
            return false;
        }
        String this$customerPhone = this.getCustomerPhone();
        String other$customerPhone = other.getCustomerPhone();
        if (this$customerPhone == null ? other$customerPhone != null : !this$customerPhone.equals(other$customerPhone)) {
            return false;
        }
        String this$shippingName = this.getShippingName();
        String other$shippingName = other.getShippingName();
        if (this$shippingName == null ? other$shippingName != null : !this$shippingName.equals(other$shippingName)) {
            return false;
        }
        String this$shippingPhone = this.getShippingPhone();
        String other$shippingPhone = other.getShippingPhone();
        if (this$shippingPhone == null ? other$shippingPhone != null : !this$shippingPhone.equals(other$shippingPhone)) {
            return false;
        }
        String this$shippingAddress1 = this.getShippingAddress1();
        String other$shippingAddress1 = other.getShippingAddress1();
        if (this$shippingAddress1 == null ? other$shippingAddress1 != null : !this$shippingAddress1.equals(other$shippingAddress1)) {
            return false;
        }
        String this$shippingAddress2 = this.getShippingAddress2();
        String other$shippingAddress2 = other.getShippingAddress2();
        if (this$shippingAddress2 == null ? other$shippingAddress2 != null : !this$shippingAddress2.equals(other$shippingAddress2)) {
            return false;
        }
        String this$shippingCity = this.getShippingCity();
        String other$shippingCity = other.getShippingCity();
        if (this$shippingCity == null ? other$shippingCity != null : !this$shippingCity.equals(other$shippingCity)) {
            return false;
        }
        String this$shippingProvince = this.getShippingProvince();
        String other$shippingProvince = other.getShippingProvince();
        if (this$shippingProvince == null ? other$shippingProvince != null : !this$shippingProvince.equals(other$shippingProvince)) {
            return false;
        }
        String this$shippingProvinceCode = this.getShippingProvinceCode();
        String other$shippingProvinceCode = other.getShippingProvinceCode();
        if (this$shippingProvinceCode == null ? other$shippingProvinceCode != null : !this$shippingProvinceCode.equals(other$shippingProvinceCode)) {
            return false;
        }
        String this$shippingCountry = this.getShippingCountry();
        String other$shippingCountry = other.getShippingCountry();
        if (this$shippingCountry == null ? other$shippingCountry != null : !this$shippingCountry.equals(other$shippingCountry)) {
            return false;
        }
        String this$shippingCountryCode = this.getShippingCountryCode();
        String other$shippingCountryCode = other.getShippingCountryCode();
        if (this$shippingCountryCode == null ? other$shippingCountryCode != null : !this$shippingCountryCode.equals(other$shippingCountryCode)) {
            return false;
        }
        String this$shippingZip = this.getShippingZip();
        String other$shippingZip = other.getShippingZip();
        if (this$shippingZip == null ? other$shippingZip != null : !this$shippingZip.equals(other$shippingZip)) {
            return false;
        }
        String this$billingName = this.getBillingName();
        String other$billingName = other.getBillingName();
        if (this$billingName == null ? other$billingName != null : !this$billingName.equals(other$billingName)) {
            return false;
        }
        String this$billingPhone = this.getBillingPhone();
        String other$billingPhone = other.getBillingPhone();
        if (this$billingPhone == null ? other$billingPhone != null : !this$billingPhone.equals(other$billingPhone)) {
            return false;
        }
        String this$billingAddress1 = this.getBillingAddress1();
        String other$billingAddress1 = other.getBillingAddress1();
        if (this$billingAddress1 == null ? other$billingAddress1 != null : !this$billingAddress1.equals(other$billingAddress1)) {
            return false;
        }
        String this$billingAddress2 = this.getBillingAddress2();
        String other$billingAddress2 = other.getBillingAddress2();
        if (this$billingAddress2 == null ? other$billingAddress2 != null : !this$billingAddress2.equals(other$billingAddress2)) {
            return false;
        }
        String this$billingCity = this.getBillingCity();
        String other$billingCity = other.getBillingCity();
        if (this$billingCity == null ? other$billingCity != null : !this$billingCity.equals(other$billingCity)) {
            return false;
        }
        String this$billingProvince = this.getBillingProvince();
        String other$billingProvince = other.getBillingProvince();
        if (this$billingProvince == null ? other$billingProvince != null : !this$billingProvince.equals(other$billingProvince)) {
            return false;
        }
        String this$billingProvinceCode = this.getBillingProvinceCode();
        String other$billingProvinceCode = other.getBillingProvinceCode();
        if (this$billingProvinceCode == null ? other$billingProvinceCode != null : !this$billingProvinceCode.equals(other$billingProvinceCode)) {
            return false;
        }
        String this$billingCountry = this.getBillingCountry();
        String other$billingCountry = other.getBillingCountry();
        if (this$billingCountry == null ? other$billingCountry != null : !this$billingCountry.equals(other$billingCountry)) {
            return false;
        }
        String this$billingCountryCode = this.getBillingCountryCode();
        String other$billingCountryCode = other.getBillingCountryCode();
        if (this$billingCountryCode == null ? other$billingCountryCode != null : !this$billingCountryCode.equals(other$billingCountryCode)) {
            return false;
        }
        String this$billingZip = this.getBillingZip();
        String other$billingZip = other.getBillingZip();
        if (this$billingZip == null ? other$billingZip != null : !this$billingZip.equals(other$billingZip)) {
            return false;
        }
        String this$sourceName = this.getSourceName();
        String other$sourceName = other.getSourceName();
        if (this$sourceName == null ? other$sourceName != null : !this$sourceName.equals(other$sourceName)) {
            return false;
        }
        String this$landingSite = this.getLandingSite();
        String other$landingSite = other.getLandingSite();
        if (this$landingSite == null ? other$landingSite != null : !this$landingSite.equals(other$landingSite)) {
            return false;
        }
        String this$referringSite = this.getReferringSite();
        String other$referringSite = other.getReferringSite();
        if (this$referringSite == null ? other$referringSite != null : !this$referringSite.equals(other$referringSite)) {
            return false;
        }
        String this$shippingMethod = this.getShippingMethod();
        String other$shippingMethod = other.getShippingMethod();
        if (this$shippingMethod == null ? other$shippingMethod != null : !this$shippingMethod.equals(other$shippingMethod)) {
            return false;
        }
        String this$discountCodes = this.getDiscountCodes();
        String other$discountCodes = other.getDiscountCodes();
        if (this$discountCodes == null ? other$discountCodes != null : !this$discountCodes.equals(other$discountCodes)) {
            return false;
        }
        String this$discountCodesJson = this.getDiscountCodesJson();
        String other$discountCodesJson = other.getDiscountCodesJson();
        if (this$discountCodesJson == null ? other$discountCodesJson != null : !this$discountCodesJson.equals(other$discountCodesJson)) {
            return false;
        }
        String this$tags = this.getTags();
        String other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) {
            return false;
        }
        String this$note = this.getNote();
        String other$note = other.getNote();
        if (this$note == null ? other$note != null : !this$note.equals(other$note)) {
            return false;
        }
        String this$orderSource = this.getOrderSource();
        String other$orderSource = other.getOrderSource();
        if (this$orderSource == null ? other$orderSource != null : !this$orderSource.equals(other$orderSource)) {
            return false;
        }
        String this$creationMode = this.getCreationMode();
        String other$creationMode = other.getCreationMode();
        if (this$creationMode == null ? other$creationMode != null : !this$creationMode.equals(other$creationMode)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        String this$influencerDiscountCode = this.getInfluencerDiscountCode();
        String other$influencerDiscountCode = other.getInfluencerDiscountCode();
        if (this$influencerDiscountCode == null ? other$influencerDiscountCode != null : !this$influencerDiscountCode.equals(other$influencerDiscountCode)) {
            return false;
        }
        String this$commissionStatus = this.getCommissionStatus();
        String other$commissionStatus = other.getCommissionStatus();
        if (this$commissionStatus == null ? other$commissionStatus != null : !this$commissionStatus.equals(other$commissionStatus)) {
            return false;
        }
        BigDecimal this$commissionAmount = this.getCommissionAmount();
        BigDecimal other$commissionAmount = other.getCommissionAmount();
        if (this$commissionAmount == null ? other$commissionAmount != null : !((Object)this$commissionAmount).equals(other$commissionAmount)) {
            return false;
        }
        BigDecimal this$commissionRate = this.getCommissionRate();
        BigDecimal other$commissionRate = other.getCommissionRate();
        if (this$commissionRate == null ? other$commissionRate != null : !((Object)this$commissionRate).equals(other$commissionRate)) {
            return false;
        }
        String this$syncStatus = this.getSyncStatus();
        String other$syncStatus = other.getSyncStatus();
        if (this$syncStatus == null ? other$syncStatus != null : !this$syncStatus.equals(other$syncStatus)) {
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
        LocalDateTime this$syncAt = this.getSyncAt();
        LocalDateTime other$syncAt = other.getSyncAt();
        if (this$syncAt == null ? other$syncAt != null : !((Object)this$syncAt).equals(other$syncAt)) {
            return false;
        }
        String this$trackingNumbers = this.getTrackingNumbers();
        String other$trackingNumbers = other.getTrackingNumbers();
        if (this$trackingNumbers == null ? other$trackingNumbers != null : !this$trackingNumbers.equals(other$trackingNumbers)) {
            return false;
        }
        String this$fulfillmentIds = this.getFulfillmentIds();
        String other$fulfillmentIds = other.getFulfillmentIds();
        if (this$fulfillmentIds == null ? other$fulfillmentIds != null : !this$fulfillmentIds.equals(other$fulfillmentIds)) {
            return false;
        }
        String this$fulfillmentsJson = this.getFulfillmentsJson();
        String other$fulfillmentsJson = other.getFulfillmentsJson();
        if (this$fulfillmentsJson == null ? other$fulfillmentsJson != null : !this$fulfillmentsJson.equals(other$fulfillmentsJson)) {
            return false;
        }
        String this$fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        String other$fulfillmentDisplayStatus = other.getFulfillmentDisplayStatus();
        if (this$fulfillmentDisplayStatus == null ? other$fulfillmentDisplayStatus != null : !this$fulfillmentDisplayStatus.equals(other$fulfillmentDisplayStatus)) {
            return false;
        }
        LocalDateTime this$deliveredAt = this.getDeliveredAt();
        LocalDateTime other$deliveredAt = other.getDeliveredAt();
        if (this$deliveredAt == null ? other$deliveredAt != null : !((Object)this$deliveredAt).equals(other$deliveredAt)) {
            return false;
        }
        LocalDateTime this$fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        LocalDateTime other$fulfillmentCreatedAt = other.getFulfillmentCreatedAt();
        if (this$fulfillmentCreatedAt == null ? other$fulfillmentCreatedAt != null : !((Object)this$fulfillmentCreatedAt).equals(other$fulfillmentCreatedAt)) {
            return false;
        }
        LocalDateTime this$inTransitAt = this.getInTransitAt();
        LocalDateTime other$inTransitAt = other.getInTransitAt();
        if (this$inTransitAt == null ? other$inTransitAt != null : !((Object)this$inTransitAt).equals(other$inTransitAt)) {
            return false;
        }
        LocalDateTime this$estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        LocalDateTime other$estimatedDeliveryAt = other.getEstimatedDeliveryAt();
        return !(this$estimatedDeliveryAt == null ? other$estimatedDeliveryAt != null : !((Object)this$estimatedDeliveryAt).equals(other$estimatedDeliveryAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EccangOrder;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object)$storeId).hashCode());
        Long $eccangOrderNumber = this.getEccangOrderNumber();
        result = result * 59 + ($eccangOrderNumber == null ? 43 : ((Object)$eccangOrderNumber).hashCode());
        Boolean $fullyPaid = this.getFullyPaid();
        result = result * 59 + ($fullyPaid == null ? 43 : ((Object)$fullyPaid).hashCode());
        Long $customerId = this.getCustomerId();
        result = result * 59 + ($customerId == null ? 43 : ((Object)$customerId).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Boolean $isDraft = this.getIsDraft();
        result = result * 59 + ($isDraft == null ? 43 : ((Object)$isDraft).hashCode());
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
        String $shopDomain = this.getShopDomain();
        result = result * 59 + ($shopDomain == null ? 43 : $shopDomain.hashCode());
        String $eccangOrderId = this.getEccangOrderId();
        result = result * 59 + ($eccangOrderId == null ? 43 : $eccangOrderId.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        LocalDateTime $createdAtEccang = this.getCreatedAtEccang();
        result = result * 59 + ($createdAtEccang == null ? 43 : ((Object)$createdAtEccang).hashCode());
        LocalDateTime $processedAtEccang = this.getProcessedAtEccang();
        result = result * 59 + ($processedAtEccang == null ? 43 : ((Object)$processedAtEccang).hashCode());
        LocalDateTime $updatedAtEccang = this.getUpdatedAtEccang();
        result = result * 59 + ($updatedAtEccang == null ? 43 : ((Object)$updatedAtEccang).hashCode());
        LocalDateTime $closedAtEccang = this.getClosedAtEccang();
        result = result * 59 + ($closedAtEccang == null ? 43 : ((Object)$closedAtEccang).hashCode());
        LocalDateTime $cancelledAtEccang = this.getCancelledAtEccang();
        result = result * 59 + ($cancelledAtEccang == null ? 43 : ((Object)$cancelledAtEccang).hashCode());
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        String $cancelReason = this.getCancelReason();
        result = result * 59 + ($cancelReason == null ? 43 : $cancelReason.hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        BigDecimal $subtotalPrice = this.getSubtotalPrice();
        result = result * 59 + ($subtotalPrice == null ? 43 : ((Object)$subtotalPrice).hashCode());
        BigDecimal $totalTax = this.getTotalTax();
        result = result * 59 + ($totalTax == null ? 43 : ((Object)$totalTax).hashCode());
        BigDecimal $totalDiscounts = this.getTotalDiscounts();
        result = result * 59 + ($totalDiscounts == null ? 43 : ((Object)$totalDiscounts).hashCode());
        BigDecimal $totalShippingPrice = this.getTotalShippingPrice();
        result = result * 59 + ($totalShippingPrice == null ? 43 : ((Object)$totalShippingPrice).hashCode());
        BigDecimal $totalPrice = this.getTotalPrice();
        result = result * 59 + ($totalPrice == null ? 43 : ((Object)$totalPrice).hashCode());
        BigDecimal $refundTotal = this.getRefundTotal();
        result = result * 59 + ($refundTotal == null ? 43 : ((Object)$refundTotal).hashCode());
        String $eccangCustomerId = this.getEccangCustomerId();
        result = result * 59 + ($eccangCustomerId == null ? 43 : $eccangCustomerId.hashCode());
        String $customerEmail = this.getCustomerEmail();
        result = result * 59 + ($customerEmail == null ? 43 : $customerEmail.hashCode());
        String $customerFirstName = this.getCustomerFirstName();
        result = result * 59 + ($customerFirstName == null ? 43 : $customerFirstName.hashCode());
        String $customerLastName = this.getCustomerLastName();
        result = result * 59 + ($customerLastName == null ? 43 : $customerLastName.hashCode());
        String $customerPhone = this.getCustomerPhone();
        result = result * 59 + ($customerPhone == null ? 43 : $customerPhone.hashCode());
        String $shippingName = this.getShippingName();
        result = result * 59 + ($shippingName == null ? 43 : $shippingName.hashCode());
        String $shippingPhone = this.getShippingPhone();
        result = result * 59 + ($shippingPhone == null ? 43 : $shippingPhone.hashCode());
        String $shippingAddress1 = this.getShippingAddress1();
        result = result * 59 + ($shippingAddress1 == null ? 43 : $shippingAddress1.hashCode());
        String $shippingAddress2 = this.getShippingAddress2();
        result = result * 59 + ($shippingAddress2 == null ? 43 : $shippingAddress2.hashCode());
        String $shippingCity = this.getShippingCity();
        result = result * 59 + ($shippingCity == null ? 43 : $shippingCity.hashCode());
        String $shippingProvince = this.getShippingProvince();
        result = result * 59 + ($shippingProvince == null ? 43 : $shippingProvince.hashCode());
        String $shippingProvinceCode = this.getShippingProvinceCode();
        result = result * 59 + ($shippingProvinceCode == null ? 43 : $shippingProvinceCode.hashCode());
        String $shippingCountry = this.getShippingCountry();
        result = result * 59 + ($shippingCountry == null ? 43 : $shippingCountry.hashCode());
        String $shippingCountryCode = this.getShippingCountryCode();
        result = result * 59 + ($shippingCountryCode == null ? 43 : $shippingCountryCode.hashCode());
        String $shippingZip = this.getShippingZip();
        result = result * 59 + ($shippingZip == null ? 43 : $shippingZip.hashCode());
        String $billingName = this.getBillingName();
        result = result * 59 + ($billingName == null ? 43 : $billingName.hashCode());
        String $billingPhone = this.getBillingPhone();
        result = result * 59 + ($billingPhone == null ? 43 : $billingPhone.hashCode());
        String $billingAddress1 = this.getBillingAddress1();
        result = result * 59 + ($billingAddress1 == null ? 43 : $billingAddress1.hashCode());
        String $billingAddress2 = this.getBillingAddress2();
        result = result * 59 + ($billingAddress2 == null ? 43 : $billingAddress2.hashCode());
        String $billingCity = this.getBillingCity();
        result = result * 59 + ($billingCity == null ? 43 : $billingCity.hashCode());
        String $billingProvince = this.getBillingProvince();
        result = result * 59 + ($billingProvince == null ? 43 : $billingProvince.hashCode());
        String $billingProvinceCode = this.getBillingProvinceCode();
        result = result * 59 + ($billingProvinceCode == null ? 43 : $billingProvinceCode.hashCode());
        String $billingCountry = this.getBillingCountry();
        result = result * 59 + ($billingCountry == null ? 43 : $billingCountry.hashCode());
        String $billingCountryCode = this.getBillingCountryCode();
        result = result * 59 + ($billingCountryCode == null ? 43 : $billingCountryCode.hashCode());
        String $billingZip = this.getBillingZip();
        result = result * 59 + ($billingZip == null ? 43 : $billingZip.hashCode());
        String $sourceName = this.getSourceName();
        result = result * 59 + ($sourceName == null ? 43 : $sourceName.hashCode());
        String $landingSite = this.getLandingSite();
        result = result * 59 + ($landingSite == null ? 43 : $landingSite.hashCode());
        String $referringSite = this.getReferringSite();
        result = result * 59 + ($referringSite == null ? 43 : $referringSite.hashCode());
        String $shippingMethod = this.getShippingMethod();
        result = result * 59 + ($shippingMethod == null ? 43 : $shippingMethod.hashCode());
        String $discountCodes = this.getDiscountCodes();
        result = result * 59 + ($discountCodes == null ? 43 : $discountCodes.hashCode());
        String $discountCodesJson = this.getDiscountCodesJson();
        result = result * 59 + ($discountCodesJson == null ? 43 : $discountCodesJson.hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
        String $note = this.getNote();
        result = result * 59 + ($note == null ? 43 : $note.hashCode());
        String $orderSource = this.getOrderSource();
        result = result * 59 + ($orderSource == null ? 43 : $orderSource.hashCode());
        String $creationMode = this.getCreationMode();
        result = result * 59 + ($creationMode == null ? 43 : $creationMode.hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        String $influencerDiscountCode = this.getInfluencerDiscountCode();
        result = result * 59 + ($influencerDiscountCode == null ? 43 : $influencerDiscountCode.hashCode());
        String $commissionStatus = this.getCommissionStatus();
        result = result * 59 + ($commissionStatus == null ? 43 : $commissionStatus.hashCode());
        BigDecimal $commissionAmount = this.getCommissionAmount();
        result = result * 59 + ($commissionAmount == null ? 43 : ((Object)$commissionAmount).hashCode());
        BigDecimal $commissionRate = this.getCommissionRate();
        result = result * 59 + ($commissionRate == null ? 43 : ((Object)$commissionRate).hashCode());
        String $syncStatus = this.getSyncStatus();
        result = result * 59 + ($syncStatus == null ? 43 : $syncStatus.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        LocalDateTime $syncAt = this.getSyncAt();
        result = result * 59 + ($syncAt == null ? 43 : ((Object)$syncAt).hashCode());
        String $trackingNumbers = this.getTrackingNumbers();
        result = result * 59 + ($trackingNumbers == null ? 43 : $trackingNumbers.hashCode());
        String $fulfillmentIds = this.getFulfillmentIds();
        result = result * 59 + ($fulfillmentIds == null ? 43 : $fulfillmentIds.hashCode());
        String $fulfillmentsJson = this.getFulfillmentsJson();
        result = result * 59 + ($fulfillmentsJson == null ? 43 : $fulfillmentsJson.hashCode());
        String $fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        result = result * 59 + ($fulfillmentDisplayStatus == null ? 43 : $fulfillmentDisplayStatus.hashCode());
        LocalDateTime $deliveredAt = this.getDeliveredAt();
        result = result * 59 + ($deliveredAt == null ? 43 : ((Object)$deliveredAt).hashCode());
        LocalDateTime $fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        result = result * 59 + ($fulfillmentCreatedAt == null ? 43 : ((Object)$fulfillmentCreatedAt).hashCode());
        LocalDateTime $inTransitAt = this.getInTransitAt();
        result = result * 59 + ($inTransitAt == null ? 43 : ((Object)$inTransitAt).hashCode());
        LocalDateTime $estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        result = result * 59 + ($estimatedDeliveryAt == null ? 43 : ((Object)$estimatedDeliveryAt).hashCode());
        return result;
    }

    public String toString() {
        return "EccangOrder(id=" + this.getId() + ", storeId=" + this.getStoreId() + ", storeName=" + this.getStoreName() + ", shopDomain=" + this.getShopDomain() + ", eccangOrderId=" + this.getEccangOrderId() + ", eccangOrderNumber=" + this.getEccangOrderNumber() + ", name=" + this.getName() + ", createdAtEccang=" + this.getCreatedAtEccang() + ", processedAtEccang=" + this.getProcessedAtEccang() + ", updatedAtEccang=" + this.getUpdatedAtEccang() + ", closedAtEccang=" + this.getClosedAtEccang() + ", cancelledAtEccang=" + this.getCancelledAtEccang() + ", financialStatus=" + this.getFinancialStatus() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", cancelReason=" + this.getCancelReason() + ", fullyPaid=" + this.getFullyPaid() + ", currency=" + this.getCurrency() + ", subtotalPrice=" + this.getSubtotalPrice() + ", totalTax=" + this.getTotalTax() + ", totalDiscounts=" + this.getTotalDiscounts() + ", totalShippingPrice=" + this.getTotalShippingPrice() + ", totalPrice=" + this.getTotalPrice() + ", refundTotal=" + this.getRefundTotal() + ", customerId=" + this.getCustomerId() + ", eccangCustomerId=" + this.getEccangCustomerId() + ", customerEmail=" + this.getCustomerEmail() + ", customerFirstName=" + this.getCustomerFirstName() + ", customerLastName=" + this.getCustomerLastName() + ", customerPhone=" + this.getCustomerPhone() + ", shippingName=" + this.getShippingName() + ", shippingPhone=" + this.getShippingPhone() + ", shippingAddress1=" + this.getShippingAddress1() + ", shippingAddress2=" + this.getShippingAddress2() + ", shippingCity=" + this.getShippingCity() + ", shippingProvince=" + this.getShippingProvince() + ", shippingProvinceCode=" + this.getShippingProvinceCode() + ", shippingCountry=" + this.getShippingCountry() + ", shippingCountryCode=" + this.getShippingCountryCode() + ", shippingZip=" + this.getShippingZip() + ", billingName=" + this.getBillingName() + ", billingPhone=" + this.getBillingPhone() + ", billingAddress1=" + this.getBillingAddress1() + ", billingAddress2=" + this.getBillingAddress2() + ", billingCity=" + this.getBillingCity() + ", billingProvince=" + this.getBillingProvince() + ", billingProvinceCode=" + this.getBillingProvinceCode() + ", billingCountry=" + this.getBillingCountry() + ", billingCountryCode=" + this.getBillingCountryCode() + ", billingZip=" + this.getBillingZip() + ", sourceName=" + this.getSourceName() + ", landingSite=" + this.getLandingSite() + ", referringSite=" + this.getReferringSite() + ", shippingMethod=" + this.getShippingMethod() + ", discountCodes=" + this.getDiscountCodes() + ", discountCodesJson=" + this.getDiscountCodesJson() + ", tags=" + this.getTags() + ", note=" + this.getNote() + ", orderSource=" + this.getOrderSource() + ", creationMode=" + this.getCreationMode() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", influencerDiscountCode=" + this.getInfluencerDiscountCode() + ", commissionStatus=" + this.getCommissionStatus() + ", commissionAmount=" + this.getCommissionAmount() + ", commissionRate=" + this.getCommissionRate() + ", syncStatus=" + this.getSyncStatus() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", syncAt=" + this.getSyncAt() + ", trackingNumbers=" + this.getTrackingNumbers() + ", fulfillmentIds=" + this.getFulfillmentIds() + ", fulfillmentsJson=" + this.getFulfillmentsJson() + ", fulfillmentDisplayStatus=" + this.getFulfillmentDisplayStatus() + ", deliveredAt=" + this.getDeliveredAt() + ", fulfillmentCreatedAt=" + this.getFulfillmentCreatedAt() + ", inTransitAt=" + this.getInTransitAt() + ", estimatedDeliveryAt=" + this.getEstimatedDeliveryAt() + ", isDraft=" + this.getIsDraft() + ")";
    }
}

