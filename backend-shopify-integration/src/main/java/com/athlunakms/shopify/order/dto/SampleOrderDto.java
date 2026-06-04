package com.athlunakms.shopify.order.dto;

import com.athlunakms.shopify.order.dto.OrderLineItemDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SampleOrderDto {
    private Long id;
    private Long orderId;
    private String shopifyOrderId;
    private Long shopifyOrderNumber;
    private String orderName;
    private String orderNo;
    private String longOrderNo;
    private Long storeId;
    private String storeName;
    private Long influencerId;
    private String influencerName;
    private BigDecimal totalPrice;
    private String currency;
    private String sampleReason;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime createdAt;
    private String financialStatus;
    private String fulfillmentStatus;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String shippingName;
    private String shippingPhone;
    private List<OrderLineItemDto> products;
    private String packageNo;
    private String fulfillmentIds;
    @JsonProperty(value="isSplit")
    private Boolean isSplit;
    private String fulfillmentDisplayStatus;
    private String trackingCompany;
    private String trackingNumber;
    private String trackingUrl;
    private String trackingEventsJson;
    private LocalDateTime cancelledAt;
    private LocalDateTime closedAt;
    private LocalDateTime processedAtShopify;
    private LocalDateTime fulfillmentCreatedAt;
    private LocalDateTime inTransitAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime estimatedDeliveryAt;
    private String recipientName;
    private String recipientAddress;
    private String recipientPhone;
    private String recipientCountry;
    private String shippingCountry;
    private Boolean autoMatched;
    @JsonProperty(value="isDraft")
    private Boolean isDraft;
    @Schema(description="\u8d1f\u8d23\u4ebaID")
    private Long ownerId;
    @Schema(description="\u8054\u7edc\u5458ID")
    private Long contactPersonId;
    @Schema(description="\u8054\u7edc\u5458\u540d\u79f0(\u4ecetags\u89e3\u6790)")
    private String contactPersonName;
    @Schema(description="负责人名称")
    private String ownerName;
    @Schema(description="合作价格(手动填写)")
    private BigDecimal cooperationPrice;

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

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getLongOrderNo() {
        return this.longOrderNo;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public String getStoreName() {
        return this.storeName;
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

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public String getShippingName() {
        return this.shippingName;
    }

    public String getShippingPhone() {
        return this.shippingPhone;
    }

    public List<OrderLineItemDto> getProducts() {
        return this.products;
    }

    public String getPackageNo() {
        return this.packageNo;
    }

    public String getFulfillmentIds() {
        return this.fulfillmentIds;
    }

    public Boolean getIsSplit() {
        return this.isSplit;
    }

    public String getFulfillmentDisplayStatus() {
        return this.fulfillmentDisplayStatus;
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

    public LocalDateTime getCancelledAt() {
        return this.cancelledAt;
    }

    public LocalDateTime getClosedAt() {
        return this.closedAt;
    }

    public LocalDateTime getProcessedAtShopify() {
        return this.processedAtShopify;
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

    public String getRecipientName() {
        return this.recipientName;
    }

    public String getRecipientAddress() {
        return this.recipientAddress;
    }

    public String getRecipientPhone() {
        return this.recipientPhone;
    }

    public String getRecipientCountry() {
        return this.recipientCountry;
    }

    public String getShippingCountry() {
        return this.shippingCountry;
    }

    public Boolean getAutoMatched() {
        return this.autoMatched;
    }

    public Boolean getIsDraft() {
        return this.isDraft;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
    }

    public String getContactPersonName() {
        return this.contactPersonName;
    }

    public String getOwnerName() {
        return this.ownerName;
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

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setShopifyOrderNumber(Long shopifyOrderNumber) {
        this.shopifyOrderNumber = shopifyOrderNumber;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setLongOrderNo(String longOrderNo) {
        this.longOrderNo = longOrderNo;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public void setProducts(List<OrderLineItemDto> products) {
        this.products = products;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public void setFulfillmentIds(String fulfillmentIds) {
        this.fulfillmentIds = fulfillmentIds;
    }

    @JsonProperty(value="isSplit")
    public void setIsSplit(Boolean isSplit) {
        this.isSplit = isSplit;
    }

    public void setFulfillmentDisplayStatus(String fulfillmentDisplayStatus) {
        this.fulfillmentDisplayStatus = fulfillmentDisplayStatus;
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

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setProcessedAtShopify(LocalDateTime processedAtShopify) {
        this.processedAtShopify = processedAtShopify;
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

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public void setRecipientCountry(String recipientCountry) {
        this.recipientCountry = recipientCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public void setAutoMatched(Boolean autoMatched) {
        this.autoMatched = autoMatched;
    }

    @JsonProperty(value="isDraft")
    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setCooperationPrice(BigDecimal cooperationPrice) {
        this.cooperationPrice = cooperationPrice;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SampleOrderDto)) {
            return false;
        }
        SampleOrderDto other = (SampleOrderDto)o;
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
        Long this$storeId = this.getStoreId();
        Long other$storeId = other.getStoreId();
        if (this$storeId == null ? other$storeId != null : !((Object)this$storeId).equals(other$storeId)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Boolean this$isSplit = this.getIsSplit();
        Boolean other$isSplit = other.getIsSplit();
        if (this$isSplit == null ? other$isSplit != null : !((Object)this$isSplit).equals(other$isSplit)) {
            return false;
        }
        Boolean this$autoMatched = this.getAutoMatched();
        Boolean other$autoMatched = other.getAutoMatched();
        if (this$autoMatched == null ? other$autoMatched != null : !((Object)this$autoMatched).equals(other$autoMatched)) {
            return false;
        }
        Boolean this$isDraft = this.getIsDraft();
        Boolean other$isDraft = other.getIsDraft();
        if (this$isDraft == null ? other$isDraft != null : !((Object)this$isDraft).equals(other$isDraft)) {
            return false;
        }
        Long this$ownerId = this.getOwnerId();
        Long other$ownerId = other.getOwnerId();
        if (this$ownerId == null ? other$ownerId != null : !((Object)this$ownerId).equals(other$ownerId)) {
            return false;
        }
        Long this$contactPersonId = this.getContactPersonId();
        Long other$contactPersonId = other.getContactPersonId();
        if (this$contactPersonId == null ? other$contactPersonId != null : !((Object)this$contactPersonId).equals(other$contactPersonId)) {
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
        String this$orderNo = this.getOrderNo();
        String other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) {
            return false;
        }
        String this$longOrderNo = this.getLongOrderNo();
        String other$longOrderNo = other.getLongOrderNo();
        if (this$longOrderNo == null ? other$longOrderNo != null : !this$longOrderNo.equals(other$longOrderNo)) {
            return false;
        }
        String this$storeName = this.getStoreName();
        String other$storeName = other.getStoreName();
        if (this$storeName == null ? other$storeName != null : !this$storeName.equals(other$storeName)) {
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
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
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
        String this$customerName = this.getCustomerName();
        String other$customerName = other.getCustomerName();
        if (this$customerName == null ? other$customerName != null : !this$customerName.equals(other$customerName)) {
            return false;
        }
        String this$customerEmail = this.getCustomerEmail();
        String other$customerEmail = other.getCustomerEmail();
        if (this$customerEmail == null ? other$customerEmail != null : !this$customerEmail.equals(other$customerEmail)) {
            return false;
        }
        String this$shippingAddress = this.getShippingAddress();
        String other$shippingAddress = other.getShippingAddress();
        if (this$shippingAddress == null ? other$shippingAddress != null : !this$shippingAddress.equals(other$shippingAddress)) {
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
        List this$products = this.getProducts();
        List other$products = other.getProducts();
        if (this$products == null ? other$products != null : !((Object)this$products).equals(other$products)) {
            return false;
        }
        String this$packageNo = this.getPackageNo();
        String other$packageNo = other.getPackageNo();
        if (this$packageNo == null ? other$packageNo != null : !this$packageNo.equals(other$packageNo)) {
            return false;
        }
        String this$fulfillmentIds = this.getFulfillmentIds();
        String other$fulfillmentIds = other.getFulfillmentIds();
        if (this$fulfillmentIds == null ? other$fulfillmentIds != null : !this$fulfillmentIds.equals(other$fulfillmentIds)) {
            return false;
        }
        String this$fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        String other$fulfillmentDisplayStatus = other.getFulfillmentDisplayStatus();
        if (this$fulfillmentDisplayStatus == null ? other$fulfillmentDisplayStatus != null : !this$fulfillmentDisplayStatus.equals(other$fulfillmentDisplayStatus)) {
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
        LocalDateTime this$cancelledAt = this.getCancelledAt();
        LocalDateTime other$cancelledAt = other.getCancelledAt();
        if (this$cancelledAt == null ? other$cancelledAt != null : !((Object)this$cancelledAt).equals(other$cancelledAt)) {
            return false;
        }
        LocalDateTime this$closedAt = this.getClosedAt();
        LocalDateTime other$closedAt = other.getClosedAt();
        if (this$closedAt == null ? other$closedAt != null : !((Object)this$closedAt).equals(other$closedAt)) {
            return false;
        }
        LocalDateTime this$processedAtShopify = this.getProcessedAtShopify();
        LocalDateTime other$processedAtShopify = other.getProcessedAtShopify();
        if (this$processedAtShopify == null ? other$processedAtShopify != null : !((Object)this$processedAtShopify).equals(other$processedAtShopify)) {
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
        LocalDateTime this$deliveredAt = this.getDeliveredAt();
        LocalDateTime other$deliveredAt = other.getDeliveredAt();
        if (this$deliveredAt == null ? other$deliveredAt != null : !((Object)this$deliveredAt).equals(other$deliveredAt)) {
            return false;
        }
        LocalDateTime this$estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        LocalDateTime other$estimatedDeliveryAt = other.getEstimatedDeliveryAt();
        if (this$estimatedDeliveryAt == null ? other$estimatedDeliveryAt != null : !((Object)this$estimatedDeliveryAt).equals(other$estimatedDeliveryAt)) {
            return false;
        }
        String this$recipientName = this.getRecipientName();
        String other$recipientName = other.getRecipientName();
        if (this$recipientName == null ? other$recipientName != null : !this$recipientName.equals(other$recipientName)) {
            return false;
        }
        String this$recipientAddress = this.getRecipientAddress();
        String other$recipientAddress = other.getRecipientAddress();
        if (this$recipientAddress == null ? other$recipientAddress != null : !this$recipientAddress.equals(other$recipientAddress)) {
            return false;
        }
        String this$recipientPhone = this.getRecipientPhone();
        String other$recipientPhone = other.getRecipientPhone();
        if (this$recipientPhone == null ? other$recipientPhone != null : !this$recipientPhone.equals(other$recipientPhone)) {
            return false;
        }
        String this$recipientCountry = this.getRecipientCountry();
        String other$recipientCountry = other.getRecipientCountry();
        if (this$recipientCountry == null ? other$recipientCountry != null : !this$recipientCountry.equals(other$recipientCountry)) {
            return false;
        }
        String this$shippingCountry = this.getShippingCountry();
        String other$shippingCountry = other.getShippingCountry();
        if (this$shippingCountry == null ? other$shippingCountry != null : !this$shippingCountry.equals(other$shippingCountry)) {
            return false;
        }
        String this$contactPersonName = this.getContactPersonName();
        String other$contactPersonName = other.getContactPersonName();
        if (this$contactPersonName == null ? other$contactPersonName != null : !this$contactPersonName.equals(other$contactPersonName)) {
            return false;
        }
        String this$ownerName = this.getOwnerName();
        String other$ownerName = other.getOwnerName();
        return !(this$ownerName == null ? other$ownerName != null : !this$ownerName.equals(other$ownerName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SampleOrderDto;
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
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object)$storeId).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Boolean $isSplit = this.getIsSplit();
        result = result * 59 + ($isSplit == null ? 43 : ((Object)$isSplit).hashCode());
        Boolean $autoMatched = this.getAutoMatched();
        result = result * 59 + ($autoMatched == null ? 43 : ((Object)$autoMatched).hashCode());
        Boolean $isDraft = this.getIsDraft();
        result = result * 59 + ($isDraft == null ? 43 : ((Object)$isDraft).hashCode());
        Long $ownerId = this.getOwnerId();
        result = result * 59 + ($ownerId == null ? 43 : ((Object)$ownerId).hashCode());
        Long $contactPersonId = this.getContactPersonId();
        result = result * 59 + ($contactPersonId == null ? 43 : ((Object)$contactPersonId).hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $orderName = this.getOrderName();
        result = result * 59 + ($orderName == null ? 43 : $orderName.hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        String $longOrderNo = this.getLongOrderNo();
        result = result * 59 + ($longOrderNo == null ? 43 : $longOrderNo.hashCode());
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
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
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        String $customerName = this.getCustomerName();
        result = result * 59 + ($customerName == null ? 43 : $customerName.hashCode());
        String $customerEmail = this.getCustomerEmail();
        result = result * 59 + ($customerEmail == null ? 43 : $customerEmail.hashCode());
        String $shippingAddress = this.getShippingAddress();
        result = result * 59 + ($shippingAddress == null ? 43 : $shippingAddress.hashCode());
        String $shippingName = this.getShippingName();
        result = result * 59 + ($shippingName == null ? 43 : $shippingName.hashCode());
        String $shippingPhone = this.getShippingPhone();
        result = result * 59 + ($shippingPhone == null ? 43 : $shippingPhone.hashCode());
        List $products = this.getProducts();
        result = result * 59 + ($products == null ? 43 : ((Object)$products).hashCode());
        String $packageNo = this.getPackageNo();
        result = result * 59 + ($packageNo == null ? 43 : $packageNo.hashCode());
        String $fulfillmentIds = this.getFulfillmentIds();
        result = result * 59 + ($fulfillmentIds == null ? 43 : $fulfillmentIds.hashCode());
        String $fulfillmentDisplayStatus = this.getFulfillmentDisplayStatus();
        result = result * 59 + ($fulfillmentDisplayStatus == null ? 43 : $fulfillmentDisplayStatus.hashCode());
        String $trackingCompany = this.getTrackingCompany();
        result = result * 59 + ($trackingCompany == null ? 43 : $trackingCompany.hashCode());
        String $trackingNumber = this.getTrackingNumber();
        result = result * 59 + ($trackingNumber == null ? 43 : $trackingNumber.hashCode());
        String $trackingUrl = this.getTrackingUrl();
        result = result * 59 + ($trackingUrl == null ? 43 : $trackingUrl.hashCode());
        String $trackingEventsJson = this.getTrackingEventsJson();
        result = result * 59 + ($trackingEventsJson == null ? 43 : $trackingEventsJson.hashCode());
        LocalDateTime $cancelledAt = this.getCancelledAt();
        result = result * 59 + ($cancelledAt == null ? 43 : ((Object)$cancelledAt).hashCode());
        LocalDateTime $closedAt = this.getClosedAt();
        result = result * 59 + ($closedAt == null ? 43 : ((Object)$closedAt).hashCode());
        LocalDateTime $processedAtShopify = this.getProcessedAtShopify();
        result = result * 59 + ($processedAtShopify == null ? 43 : ((Object)$processedAtShopify).hashCode());
        LocalDateTime $fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        result = result * 59 + ($fulfillmentCreatedAt == null ? 43 : ((Object)$fulfillmentCreatedAt).hashCode());
        LocalDateTime $inTransitAt = this.getInTransitAt();
        result = result * 59 + ($inTransitAt == null ? 43 : ((Object)$inTransitAt).hashCode());
        LocalDateTime $deliveredAt = this.getDeliveredAt();
        result = result * 59 + ($deliveredAt == null ? 43 : ((Object)$deliveredAt).hashCode());
        LocalDateTime $estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        result = result * 59 + ($estimatedDeliveryAt == null ? 43 : ((Object)$estimatedDeliveryAt).hashCode());
        String $recipientName = this.getRecipientName();
        result = result * 59 + ($recipientName == null ? 43 : $recipientName.hashCode());
        String $recipientAddress = this.getRecipientAddress();
        result = result * 59 + ($recipientAddress == null ? 43 : $recipientAddress.hashCode());
        String $recipientPhone = this.getRecipientPhone();
        result = result * 59 + ($recipientPhone == null ? 43 : $recipientPhone.hashCode());
        String $recipientCountry = this.getRecipientCountry();
        result = result * 59 + ($recipientCountry == null ? 43 : $recipientCountry.hashCode());
        String $shippingCountry = this.getShippingCountry();
        result = result * 59 + ($shippingCountry == null ? 43 : $shippingCountry.hashCode());
        String $contactPersonName = this.getContactPersonName();
        result = result * 59 + ($contactPersonName == null ? 43 : $contactPersonName.hashCode());
        String $ownerName = this.getOwnerName();
        result = result * 59 + ($ownerName == null ? 43 : $ownerName.hashCode());
        return result;
    }

    public String toString() {
        return "SampleOrderDto(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber() + ", orderName=" + this.getOrderName() + ", orderNo=" + this.getOrderNo() + ", longOrderNo=" + this.getLongOrderNo() + ", storeId=" + this.getStoreId() + ", storeName=" + this.getStoreName() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", totalPrice=" + this.getTotalPrice() + ", currency=" + this.getCurrency() + ", sampleReason=" + this.getSampleReason() + ", orderCreatedAt=" + this.getOrderCreatedAt() + ", createdAt=" + this.getCreatedAt() + ", financialStatus=" + this.getFinancialStatus() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", customerName=" + this.getCustomerName() + ", customerEmail=" + this.getCustomerEmail() + ", shippingAddress=" + this.getShippingAddress() + ", shippingName=" + this.getShippingName() + ", shippingPhone=" + this.getShippingPhone() + ", products=" + this.getProducts() + ", packageNo=" + this.getPackageNo() + ", fulfillmentIds=" + this.getFulfillmentIds() + ", isSplit=" + this.getIsSplit() + ", fulfillmentDisplayStatus=" + this.getFulfillmentDisplayStatus() + ", trackingCompany=" + this.getTrackingCompany() + ", trackingNumber=" + this.getTrackingNumber() + ", trackingUrl=" + this.getTrackingUrl() + ", trackingEventsJson=" + this.getTrackingEventsJson() + ", cancelledAt=" + this.getCancelledAt() + ", closedAt=" + this.getClosedAt() + ", processedAtShopify=" + this.getProcessedAtShopify() + ", fulfillmentCreatedAt=" + this.getFulfillmentCreatedAt() + ", inTransitAt=" + this.getInTransitAt() + ", deliveredAt=" + this.getDeliveredAt() + ", estimatedDeliveryAt=" + this.getEstimatedDeliveryAt() + ", recipientName=" + this.getRecipientName() + ", recipientAddress=" + this.getRecipientAddress() + ", recipientPhone=" + this.getRecipientPhone() + ", recipientCountry=" + this.getRecipientCountry() + ", shippingCountry=" + this.getShippingCountry() + ", autoMatched=" + this.getAutoMatched() + ", isDraft=" + this.getIsDraft() + ", ownerId=" + this.getOwnerId() + ", contactPersonId=" + this.getContactPersonId() + ", contactPersonName=" + this.getContactPersonName() + ", ownerName=" + this.getOwnerName() + ")";
    }
}

