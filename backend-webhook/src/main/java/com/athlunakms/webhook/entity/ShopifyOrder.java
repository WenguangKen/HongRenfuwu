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
@Table(name = "orders")
public class ShopifyOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "shopify_order_id", unique = true, length = 100)
    private String shopifyOrderId;
    @Column(name = "store_id")
    private Long storeId;
    @Column(name = "order_number")
    private Long shopifyOrderNumber;
    @Column(name = "shopify_order_number")
    private Long shopifyOrderNumberLegacy;
    @Column(name = "name")
    private String orderName;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "customer_first_name")
    private String customerFirstName;
    @Column(name = "customer_last_name")
    private String customerLastName;
    @Column(name = "customer_phone")
    private String customerPhone;
    @Column(name = "financial_status")
    private String financialStatus;
    @Column(name = "fulfillment_status")
    private String fulfillmentStatus;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "subtotal")
    private BigDecimal subtotalPrice;
    @Column(name = "total_tax")
    private BigDecimal totalTax;
    @Column(name = "total_discount")
    private BigDecimal totalDiscounts;
    @Column(name = "total_shipping_price")
    private BigDecimal totalShippingPrice;
    @Column(name = "refund_total")
    private BigDecimal refundTotal;
    @Column(name = "currency")
    private String currency;
    @Column(name = "discount_codes", columnDefinition = "TEXT")
    private String discountCodes;
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "tags")
    private String tags;
    @Column(name = "shipping_name")
    private String shippingName;
    @Column(name = "shipping_phone")
    private String shippingPhone;
    @Column(name = "shipping_address1")
    private String shippingAddress1;
    @Column(name = "shipping_address2")
    private String shippingAddress2;
    @Column(name = "shipping_city")
    private String shippingCity;
    @Column(name = "shipping_province")
    private String shippingProvince;
    @Column(name = "shipping_province_code")
    private String shippingProvinceCode;
    @Column(name = "shipping_country")
    private String shippingCountry;
    @Column(name = "shipping_country_code")
    private String shippingCountryCode;
    @Column(name = "shipping_zip")
    private String shippingZip;
    @Column(name = "tracking_numbers")
    private String trackingNumbers;
    @Column(name = "fulfillment_ids")
    private String fulfillmentIds;
    @Column(name = "fulfillments_json", columnDefinition = "JSON")
    private String fulfillmentsJson;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    @Column(name = "processed_at_shopify")
    private LocalDateTime processedAtShopify;
    @Column(name = "order_date")
    private LocalDateTime createdAtShopify;
    @Column(name = "updated_at_shopify")
    private LocalDateTime updatedAtShopify;
    @Column(name = "synced_at")
    private LocalDateTime syncedAt;
    @Column(name = "fulfillment_display_status", length = 100)
    private String fulfillmentDisplayStatus;
    @Column(name = "fulfillment_created_at")
    private LocalDateTime fulfillmentCreatedAt;
    @Column(name = "in_transit_at")
    private LocalDateTime inTransitAt;
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    @Column(name = "estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;
    @Column(name = "source_name")
    private String orderSource;

    public String getEmail() {
        return this.customerEmail;
    }

    public Long getId() {
        return this.id;
    }

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public Long getShopifyOrderNumber() {
        return this.shopifyOrderNumber;
    }

    public Long getShopifyOrderNumberLegacy() {
        return this.shopifyOrderNumberLegacy;
    }

    public String getOrderName() {
        return this.orderName;
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

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
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

    public BigDecimal getRefundTotal() {
        return this.refundTotal;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getDiscountCodes() {
        return this.discountCodes;
    }

    public String getNote() {
        return this.note;
    }

    public String getTags() {
        return this.tags;
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

    public String getTrackingNumbers() {
        return this.trackingNumbers;
    }

    public String getFulfillmentIds() {
        return this.fulfillmentIds;
    }

    public String getFulfillmentsJson() {
        return this.fulfillmentsJson;
    }

    public LocalDateTime getCancelledAt() {
        return this.cancelledAt;
    }

    public LocalDateTime getClosedAt() {
        return this.closedAt;
    }

    public LocalDateTime getProcessedAtShopify() {
        return this.processedAtShopify;
    }

    public LocalDateTime getCreatedAtShopify() {
        return this.createdAtShopify;
    }

    public LocalDateTime getUpdatedAtShopify() {
        return this.updatedAtShopify;
    }

    public LocalDateTime getSyncedAt() {
        return this.syncedAt;
    }

    public String getFulfillmentDisplayStatus() {
        return this.fulfillmentDisplayStatus;
    }

    public LocalDateTime getFulfillmentCreatedAt() {
        return this.fulfillmentCreatedAt;
    }

    public LocalDateTime getInTransitAt() {
        return this.inTransitAt;
    }

    public LocalDateTime getDeliveredAt() {
        return this.deliveredAt;
    }

    public LocalDateTime getEstimatedDeliveryAt() {
        return this.estimatedDeliveryAt;
    }

    public String getOrderSource() {
        return this.orderSource;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setShopifyOrderNumber(Long shopifyOrderNumber) {
        this.shopifyOrderNumber = shopifyOrderNumber;
    }

    public void setShopifyOrderNumberLegacy(Long shopifyOrderNumberLegacy) {
        this.shopifyOrderNumberLegacy = shopifyOrderNumberLegacy;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
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

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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

    public void setRefundTotal(BigDecimal refundTotal) {
        this.refundTotal = refundTotal;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDiscountCodes(String discountCodes) {
        this.discountCodes = discountCodes;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public void setTrackingNumbers(String trackingNumbers) {
        this.trackingNumbers = trackingNumbers;
    }

    public void setFulfillmentIds(String fulfillmentIds) {
        this.fulfillmentIds = fulfillmentIds;
    }

    public void setFulfillmentsJson(String fulfillmentsJson) {
        this.fulfillmentsJson = fulfillmentsJson;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setProcessedAtShopify(LocalDateTime processedAtShopify) {
        this.processedAtShopify = processedAtShopify;
    }

    public void setCreatedAtShopify(LocalDateTime createdAtShopify) {
        this.createdAtShopify = createdAtShopify;
    }

    public void setUpdatedAtShopify(LocalDateTime updatedAtShopify) {
        this.updatedAtShopify = updatedAtShopify;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }

    public void setFulfillmentDisplayStatus(String fulfillmentDisplayStatus) {
        this.fulfillmentDisplayStatus = fulfillmentDisplayStatus;
    }

    public void setFulfillmentCreatedAt(LocalDateTime fulfillmentCreatedAt) {
        this.fulfillmentCreatedAt = fulfillmentCreatedAt;
    }

    public void setInTransitAt(LocalDateTime inTransitAt) {
        this.inTransitAt = inTransitAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setEstimatedDeliveryAt(LocalDateTime estimatedDeliveryAt) {
        this.estimatedDeliveryAt = estimatedDeliveryAt;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyOrder)) {
            return false;
        }
        ShopifyOrder other = (ShopifyOrder) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object) this$id).equals(other$id)) {
            return false;
        }
        Long this$storeId = this.getStoreId();
        Long other$storeId = other.getStoreId();
        if (this$storeId == null ? other$storeId != null : !((Object) this$storeId).equals(other$storeId)) {
            return false;
        }
        Long this$shopifyOrderNumber = this.getShopifyOrderNumber();
        Long other$shopifyOrderNumber = other.getShopifyOrderNumber();
        if (this$shopifyOrderNumber == null ? other$shopifyOrderNumber != null
                : !((Object) this$shopifyOrderNumber).equals(other$shopifyOrderNumber)) {
            return false;
        }
        Long this$shopifyOrderNumberLegacy = this.getShopifyOrderNumberLegacy();
        Long other$shopifyOrderNumberLegacy = other.getShopifyOrderNumberLegacy();
        if (this$shopifyOrderNumberLegacy == null ? other$shopifyOrderNumberLegacy != null
                : !((Object) this$shopifyOrderNumberLegacy).equals(other$shopifyOrderNumberLegacy)) {
            return false;
        }
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null
                : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
            return false;
        }
        String this$orderName = this.getOrderName();
        String other$orderName = other.getOrderName();
        if (this$orderName == null ? other$orderName != null : !this$orderName.equals(other$orderName)) {
            return false;
        }
        String this$customerEmail = this.getCustomerEmail();
        String other$customerEmail = other.getCustomerEmail();
        if (this$customerEmail == null ? other$customerEmail != null
                : !this$customerEmail.equals(other$customerEmail)) {
            return false;
        }
        String this$customerFirstName = this.getCustomerFirstName();
        String other$customerFirstName = other.getCustomerFirstName();
        if (this$customerFirstName == null ? other$customerFirstName != null
                : !this$customerFirstName.equals(other$customerFirstName)) {
            return false;
        }
        String this$customerLastName = this.getCustomerLastName();
        String other$customerLastName = other.getCustomerLastName();
        if (this$customerLastName == null ? other$customerLastName != null
                : !this$customerLastName.equals(other$customerLastName)) {
            return false;
        }
        String this$customerPhone = this.getCustomerPhone();
        String other$customerPhone = other.getCustomerPhone();
        if (this$customerPhone == null ? other$customerPhone != null
                : !this$customerPhone.equals(other$customerPhone)) {
            return false;
        }
        String this$financialStatus = this.getFinancialStatus();
        String other$financialStatus = other.getFinancialStatus();
        if (this$financialStatus == null ? other$financialStatus != null
                : !this$financialStatus.equals(other$financialStatus)) {
            return false;
        }
        String this$fulfillmentStatus = this.getFulfillmentStatus();
        String other$fulfillmentStatus = other.getFulfillmentStatus();
        if (this$fulfillmentStatus == null ? other$fulfillmentStatus != null
                : !this$fulfillmentStatus.equals(other$fulfillmentStatus)) {
            return false;
        }
        BigDecimal this$totalPrice = this.getTotalPrice();
        BigDecimal other$totalPrice = other.getTotalPrice();
        if (this$totalPrice == null ? other$totalPrice != null : !((Object) this$totalPrice).equals(other$totalPrice)) {
            return false;
        }
        BigDecimal this$subtotalPrice = this.getSubtotalPrice();
        BigDecimal other$subtotalPrice = other.getSubtotalPrice();
        if (this$subtotalPrice == null ? other$subtotalPrice != null
                : !((Object) this$subtotalPrice).equals(other$subtotalPrice)) {
            return false;
        }
        BigDecimal this$totalTax = this.getTotalTax();
        BigDecimal other$totalTax = other.getTotalTax();
        if (this$totalTax == null ? other$totalTax != null : !((Object) this$totalTax).equals(other$totalTax)) {
            return false;
        }
        BigDecimal this$totalDiscounts = this.getTotalDiscounts();
        BigDecimal other$totalDiscounts = other.getTotalDiscounts();
        if (this$totalDiscounts == null ? other$totalDiscounts != null
                : !((Object) this$totalDiscounts).equals(other$totalDiscounts)) {
            return false;
        }
        BigDecimal this$totalShippingPrice = this.getTotalShippingPrice();
        BigDecimal other$totalShippingPrice = other.getTotalShippingPrice();
        if (this$totalShippingPrice == null ? other$totalShippingPrice != null
                : !((Object) this$totalShippingPrice).equals(other$totalShippingPrice)) {
            return false;
        }
        BigDecimal this$refundTotal = this.getRefundTotal();
        BigDecimal other$refundTotal = other.getRefundTotal();
        if (this$refundTotal == null ? other$refundTotal != null
                : !((Object) this$refundTotal).equals(other$refundTotal)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$discountCodes = this.getDiscountCodes();
        String other$discountCodes = other.getDiscountCodes();
        if (this$discountCodes == null ? other$discountCodes != null
                : !this$discountCodes.equals(other$discountCodes)) {
            return false;
        }
        String this$note = this.getNote();
        String other$note = other.getNote();
        if (this$note == null ? other$note != null : !this$note.equals(other$note)) {
            return false;
        }
        String this$tags = this.getTags();
        String other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) {
            return false;
        }
        String this$shippingName = this.getShippingName();
        String other$shippingName = other.getShippingName();
        if (this$shippingName == null ? other$shippingName != null : !this$shippingName.equals(other$shippingName)) {
            return false;
        }
        String this$shippingPhone = this.getShippingPhone();
        String other$shippingPhone = other.getShippingPhone();
        if (this$shippingPhone == null ? other$shippingPhone != null
                : !this$shippingPhone.equals(other$shippingPhone)) {
            return false;
        }
        String this$shippingAddress1 = this.getShippingAddress1();
        String other$shippingAddress1 = other.getShippingAddress1();
        if (this$shippingAddress1 == null ? other$shippingAddress1 != null
                : !this$shippingAddress1.equals(other$shippingAddress1)) {
            return false;
        }
        String this$shippingAddress2 = this.getShippingAddress2();
        String other$shippingAddress2 = other.getShippingAddress2();
        if (this$shippingAddress2 == null ? other$shippingAddress2 != null
                : !this$shippingAddress2.equals(other$shippingAddress2)) {
            return false;
        }
        String this$shippingCity = this.getShippingCity();
        String other$shippingCity = other.getShippingCity();
        if (this$shippingCity == null ? other$shippingCity != null : !this$shippingCity.equals(other$shippingCity)) {
            return false;
        }
        String this$shippingProvince = this.getShippingProvince();
        String other$shippingProvince = other.getShippingProvince();
        if (this$shippingProvince == null ? other$shippingProvince != null
                : !this$shippingProvince.equals(other$shippingProvince)) {
            return false;
        }
        String this$shippingProvinceCode = this.getShippingProvinceCode();
        String other$shippingProvinceCode = other.getShippingProvinceCode();
        if (this$shippingProvinceCode == null ? other$shippingProvinceCode != null
                : !this$shippingProvinceCode.equals(other$shippingProvinceCode)) {
            return false;
        }
        String this$shippingCountry = this.getShippingCountry();
        String other$shippingCountry = other.getShippingCountry();
        if (this$shippingCountry == null ? other$shippingCountry != null
                : !this$shippingCountry.equals(other$shippingCountry)) {
            return false;
        }
        String this$shippingCountryCode = this.getShippingCountryCode();
        String other$shippingCountryCode = other.getShippingCountryCode();
        if (this$shippingCountryCode == null ? other$shippingCountryCode != null
                : !this$shippingCountryCode.equals(other$shippingCountryCode)) {
            return false;
        }
        String this$shippingZip = this.getShippingZip();
        String other$shippingZip = other.getShippingZip();
        if (this$shippingZip == null ? other$shippingZip != null : !this$shippingZip.equals(other$shippingZip)) {
            return false;
        }
        String this$trackingNumbers = this.getTrackingNumbers();
        String other$trackingNumbers = other.getTrackingNumbers();
        if (this$trackingNumbers == null ? other$trackingNumbers != null
                : !this$trackingNumbers.equals(other$trackingNumbers)) {
            return false;
        }
        String this$fulfillmentIds = this.getFulfillmentIds();
        String other$fulfillmentIds = other.getFulfillmentIds();
        if (this$fulfillmentIds == null ? other$fulfillmentIds != null
                : !this$fulfillmentIds.equals(other$fulfillmentIds)) {
            return false;
        }
        String this$fulfillmentsJson = this.getFulfillmentsJson();
        String other$fulfillmentsJson = other.getFulfillmentsJson();
        if (this$fulfillmentsJson == null ? other$fulfillmentsJson != null
                : !this$fulfillmentsJson.equals(other$fulfillmentsJson)) {
            return false;
        }
        LocalDateTime this$cancelledAt = this.getCancelledAt();
        LocalDateTime other$cancelledAt = other.getCancelledAt();
        if (this$cancelledAt == null ? other$cancelledAt != null
                : !((Object) this$cancelledAt).equals(other$cancelledAt)) {
            return false;
        }
        LocalDateTime this$closedAt = this.getClosedAt();
        LocalDateTime other$closedAt = other.getClosedAt();
        if (this$closedAt == null ? other$closedAt != null : !((Object) this$closedAt).equals(other$closedAt)) {
            return false;
        }
        LocalDateTime this$processedAtShopify = this.getProcessedAtShopify();
        LocalDateTime other$processedAtShopify = other.getProcessedAtShopify();
        if (this$processedAtShopify == null ? other$processedAtShopify != null
                : !((Object) this$processedAtShopify).equals(other$processedAtShopify)) {
            return false;
        }
        LocalDateTime this$createdAtShopify = this.getCreatedAtShopify();
        LocalDateTime other$createdAtShopify = other.getCreatedAtShopify();
        if (this$createdAtShopify == null ? other$createdAtShopify != null
                : !((Object) this$createdAtShopify).equals(other$createdAtShopify)) {
            return false;
        }
        LocalDateTime this$updatedAtShopify = this.getUpdatedAtShopify();
        LocalDateTime other$updatedAtShopify = other.getUpdatedAtShopify();
        if (this$updatedAtShopify == null ? other$updatedAtShopify != null
                : !((Object) this$updatedAtShopify).equals(other$updatedAtShopify)) {
            return false;
        }
        LocalDateTime this$syncedAt = this.getSyncedAt();
        LocalDateTime other$syncedAt = other.getSyncedAt();
        if (this$syncedAt == null ? other$syncedAt != null : !((Object) this$syncedAt).equals(other$syncedAt)) {
            return false;
        }
        String this$fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        String other$fulfillmentDisplayStatus = other.getFulfillmentDisplayStatus();
        if (this$fulfillmentDisplayStatus == null ? other$fulfillmentDisplayStatus != null
                : !this$fulfillmentDisplayStatus.equals(other$fulfillmentDisplayStatus)) {
            return false;
        }
        LocalDateTime this$fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        LocalDateTime other$fulfillmentCreatedAt = other.getFulfillmentCreatedAt();
        if (this$fulfillmentCreatedAt == null ? other$fulfillmentCreatedAt != null
                : !((Object) this$fulfillmentCreatedAt).equals(other$fulfillmentCreatedAt)) {
            return false;
        }
        LocalDateTime this$inTransitAt = this.getInTransitAt();
        LocalDateTime other$inTransitAt = other.getInTransitAt();
        if (this$inTransitAt == null ? other$inTransitAt != null
                : !((Object) this$inTransitAt).equals(other$inTransitAt)) {
            return false;
        }
        LocalDateTime this$deliveredAt = this.getDeliveredAt();
        LocalDateTime other$deliveredAt = other.getDeliveredAt();
        if (this$deliveredAt == null ? other$deliveredAt != null
                : !((Object) this$deliveredAt).equals(other$deliveredAt)) {
            return false;
        }
        LocalDateTime this$estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        LocalDateTime other$estimatedDeliveryAt = other.getEstimatedDeliveryAt();
        return !(this$estimatedDeliveryAt == null ? other$estimatedDeliveryAt != null
                : !((Object) this$estimatedDeliveryAt).equals(other$estimatedDeliveryAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyOrder;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object) $id).hashCode());
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object) $storeId).hashCode());
        Long $shopifyOrderNumber = this.getShopifyOrderNumber();
        result = result * 59 + ($shopifyOrderNumber == null ? 43 : ((Object) $shopifyOrderNumber).hashCode());
        Long $shopifyOrderNumberLegacy = this.getShopifyOrderNumberLegacy();
        result = result * 59
                + ($shopifyOrderNumberLegacy == null ? 43 : ((Object) $shopifyOrderNumberLegacy).hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $orderName = this.getOrderName();
        result = result * 59 + ($orderName == null ? 43 : $orderName.hashCode());
        String $customerEmail = this.getCustomerEmail();
        result = result * 59 + ($customerEmail == null ? 43 : $customerEmail.hashCode());
        String $customerFirstName = this.getCustomerFirstName();
        result = result * 59 + ($customerFirstName == null ? 43 : $customerFirstName.hashCode());
        String $customerLastName = this.getCustomerLastName();
        result = result * 59 + ($customerLastName == null ? 43 : $customerLastName.hashCode());
        String $customerPhone = this.getCustomerPhone();
        result = result * 59 + ($customerPhone == null ? 43 : $customerPhone.hashCode());
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        BigDecimal $totalPrice = this.getTotalPrice();
        result = result * 59 + ($totalPrice == null ? 43 : ((Object) $totalPrice).hashCode());
        BigDecimal $subtotalPrice = this.getSubtotalPrice();
        result = result * 59 + ($subtotalPrice == null ? 43 : ((Object) $subtotalPrice).hashCode());
        BigDecimal $totalTax = this.getTotalTax();
        result = result * 59 + ($totalTax == null ? 43 : ((Object) $totalTax).hashCode());
        BigDecimal $totalDiscounts = this.getTotalDiscounts();
        result = result * 59 + ($totalDiscounts == null ? 43 : ((Object) $totalDiscounts).hashCode());
        BigDecimal $totalShippingPrice = this.getTotalShippingPrice();
        result = result * 59 + ($totalShippingPrice == null ? 43 : ((Object) $totalShippingPrice).hashCode());
        BigDecimal $refundTotal = this.getRefundTotal();
        result = result * 59 + ($refundTotal == null ? 43 : ((Object) $refundTotal).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $discountCodes = this.getDiscountCodes();
        result = result * 59 + ($discountCodes == null ? 43 : $discountCodes.hashCode());
        String $note = this.getNote();
        result = result * 59 + ($note == null ? 43 : $note.hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
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
        String $trackingNumbers = this.getTrackingNumbers();
        result = result * 59 + ($trackingNumbers == null ? 43 : $trackingNumbers.hashCode());
        String $fulfillmentIds = this.getFulfillmentIds();
        result = result * 59 + ($fulfillmentIds == null ? 43 : $fulfillmentIds.hashCode());
        String $fulfillmentsJson = this.getFulfillmentsJson();
        result = result * 59 + ($fulfillmentsJson == null ? 43 : $fulfillmentsJson.hashCode());
        LocalDateTime $cancelledAt = this.getCancelledAt();
        result = result * 59 + ($cancelledAt == null ? 43 : ((Object) $cancelledAt).hashCode());
        LocalDateTime $closedAt = this.getClosedAt();
        result = result * 59 + ($closedAt == null ? 43 : ((Object) $closedAt).hashCode());
        LocalDateTime $processedAtShopify = this.getProcessedAtShopify();
        result = result * 59 + ($processedAtShopify == null ? 43 : ((Object) $processedAtShopify).hashCode());
        LocalDateTime $createdAtShopify = this.getCreatedAtShopify();
        result = result * 59 + ($createdAtShopify == null ? 43 : ((Object) $createdAtShopify).hashCode());
        LocalDateTime $updatedAtShopify = this.getUpdatedAtShopify();
        result = result * 59 + ($updatedAtShopify == null ? 43 : ((Object) $updatedAtShopify).hashCode());
        LocalDateTime $syncedAt = this.getSyncedAt();
        result = result * 59 + ($syncedAt == null ? 43 : ((Object) $syncedAt).hashCode());
        String $fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        result = result * 59 + ($fulfillmentDisplayStatus == null ? 43 : $fulfillmentDisplayStatus.hashCode());
        LocalDateTime $fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        result = result * 59 + ($fulfillmentCreatedAt == null ? 43 : ((Object) $fulfillmentCreatedAt).hashCode());
        LocalDateTime $inTransitAt = this.getInTransitAt();
        result = result * 59 + ($inTransitAt == null ? 43 : ((Object) $inTransitAt).hashCode());
        LocalDateTime $deliveredAt = this.getDeliveredAt();
        result = result * 59 + ($deliveredAt == null ? 43 : ((Object) $deliveredAt).hashCode());
        LocalDateTime $estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        result = result * 59 + ($estimatedDeliveryAt == null ? 43 : ((Object) $estimatedDeliveryAt).hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyOrder(id=" + this.getId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", storeId="
                + this.getStoreId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber()
                + ", shopifyOrderNumberLegacy=" + this.getShopifyOrderNumberLegacy() + ", orderName="
                + this.getOrderName() + ", customerEmail=" + this.getCustomerEmail() + ", customerFirstName="
                + this.getCustomerFirstName() + ", customerLastName=" + this.getCustomerLastName() + ", customerPhone="
                + this.getCustomerPhone() + ", financialStatus=" + this.getFinancialStatus() + ", fulfillmentStatus="
                + this.getFulfillmentStatus() + ", totalPrice=" + this.getTotalPrice() + ", subtotalPrice="
                + this.getSubtotalPrice() + ", totalTax=" + this.getTotalTax() + ", totalDiscounts="
                + this.getTotalDiscounts() + ", totalShippingPrice=" + this.getTotalShippingPrice() + ", refundTotal="
                + this.getRefundTotal() + ", currency=" + this.getCurrency() + ", discountCodes="
                + this.getDiscountCodes() + ", note=" + this.getNote() + ", tags=" + this.getTags() + ", shippingName="
                + this.getShippingName() + ", shippingPhone=" + this.getShippingPhone() + ", shippingAddress1="
                + this.getShippingAddress1() + ", shippingAddress2=" + this.getShippingAddress2() + ", shippingCity="
                + this.getShippingCity() + ", shippingProvince=" + this.getShippingProvince()
                + ", shippingProvinceCode=" + this.getShippingProvinceCode() + ", shippingCountry="
                + this.getShippingCountry() + ", shippingCountryCode=" + this.getShippingCountryCode()
                + ", shippingZip=" + this.getShippingZip() + ", trackingNumbers=" + this.getTrackingNumbers()
                + ", fulfillmentIds=" + this.getFulfillmentIds() + ", fulfillmentsJson=" + this.getFulfillmentsJson()
                + ", cancelledAt=" + this.getCancelledAt() + ", closedAt=" + this.getClosedAt()
                + ", processedAtShopify=" + this.getProcessedAtShopify() + ", createdAtShopify="
                + this.getCreatedAtShopify() + ", updatedAtShopify=" + this.getUpdatedAtShopify() + ", syncedAt="
                + this.getSyncedAt() + ", fulfillmentDisplayStatus=" + this.getFulfillmentDisplayStatus()
                + ", fulfillmentCreatedAt=" + this.getFulfillmentCreatedAt() + ", inTransitAt=" + this.getInTransitAt()
                + ", deliveredAt=" + this.getDeliveredAt() + ", estimatedDeliveryAt=" + this.getEstimatedDeliveryAt()
                + ")";
    }
}
