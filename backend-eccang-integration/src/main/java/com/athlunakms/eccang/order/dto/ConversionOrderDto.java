package com.athlunakms.eccang.order.dto;

import com.athlunakms.eccang.order.dto.OrderLineItemDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ConversionOrderDto {
    private Long id;
    private Long orderId;
    private String eccangOrderId;
    private Long eccangOrderNumber;
    private String orderName;
    private String orderNo;
    private String longOrderNo;
    private Long storeId;
    private String storeName;
    private Long influencerId;
    private String influencerName;
    private String discountCode;
    private BigDecimal totalPrice;
    private BigDecimal totalShipping;
    private BigDecimal totalRefund;
    private BigDecimal commissionableAmount;
    private String currency;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private String commissionStatus;
    private String financialStatus;
    private String fulfillmentStatus;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String shippingName;
    private String shippingPhone;
    private String shippingCountry;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientCountry;
    private List<OrderLineItemDto> products;
    private String packageNo;
    private String fulfillmentIds;
    private Boolean isSplit;
    private String fulfillmentDisplayStatus;
    private String trackingCompany;
    private String trackingNumber;
    private String trackingUrl;
    private String trackingEventsJson;
    private LocalDateTime cancelledAt;
    private LocalDateTime closedAt;
    private LocalDateTime processedAtEccang;
    private LocalDateTime fulfillmentCreatedAt;
    private LocalDateTime inTransitAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime estimatedDeliveryAt;

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

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
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

    public String getShippingCountry() {
        return this.shippingCountry;
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

    public LocalDateTime getProcessedAtEccang() {
        return this.processedAtEccang;
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

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
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

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
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

    public void setProducts(List<OrderLineItemDto> products) {
        this.products = products;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public void setFulfillmentIds(String fulfillmentIds) {
        this.fulfillmentIds = fulfillmentIds;
    }

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

    public void setProcessedAtEccang(LocalDateTime processedAtEccang) {
        this.processedAtEccang = processedAtEccang;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConversionOrderDto)) {
            return false;
        }
        ConversionOrderDto other = (ConversionOrderDto)o;
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
        String this$shippingCountry = this.getShippingCountry();
        String other$shippingCountry = other.getShippingCountry();
        if (this$shippingCountry == null ? other$shippingCountry != null : !this$shippingCountry.equals(other$shippingCountry)) {
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
        LocalDateTime this$processedAtEccang = this.getProcessedAtEccang();
        LocalDateTime other$processedAtEccang = other.getProcessedAtEccang();
        if (this$processedAtEccang == null ? other$processedAtEccang != null : !((Object)this$processedAtEccang).equals(other$processedAtEccang)) {
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
        return !(this$estimatedDeliveryAt == null ? other$estimatedDeliveryAt != null : !((Object)this$estimatedDeliveryAt).equals(other$estimatedDeliveryAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConversionOrderDto;
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
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object)$storeId).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Boolean $isSplit = this.getIsSplit();
        result = result * 59 + ($isSplit == null ? 43 : ((Object)$isSplit).hashCode());
        String $eccangOrderId = this.getEccangOrderId();
        result = result * 59 + ($eccangOrderId == null ? 43 : $eccangOrderId.hashCode());
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
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        LocalDateTime $orderCreatedAt = this.getOrderCreatedAt();
        result = result * 59 + ($orderCreatedAt == null ? 43 : ((Object)$orderCreatedAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
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
        String $shippingCountry = this.getShippingCountry();
        result = result * 59 + ($shippingCountry == null ? 43 : $shippingCountry.hashCode());
        String $recipientName = this.getRecipientName();
        result = result * 59 + ($recipientName == null ? 43 : $recipientName.hashCode());
        String $recipientPhone = this.getRecipientPhone();
        result = result * 59 + ($recipientPhone == null ? 43 : $recipientPhone.hashCode());
        String $recipientAddress = this.getRecipientAddress();
        result = result * 59 + ($recipientAddress == null ? 43 : $recipientAddress.hashCode());
        String $recipientCountry = this.getRecipientCountry();
        result = result * 59 + ($recipientCountry == null ? 43 : $recipientCountry.hashCode());
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
        LocalDateTime $processedAtEccang = this.getProcessedAtEccang();
        result = result * 59 + ($processedAtEccang == null ? 43 : ((Object)$processedAtEccang).hashCode());
        LocalDateTime $fulfillmentCreatedAt = this.getFulfillmentCreatedAt();
        result = result * 59 + ($fulfillmentCreatedAt == null ? 43 : ((Object)$fulfillmentCreatedAt).hashCode());
        LocalDateTime $inTransitAt = this.getInTransitAt();
        result = result * 59 + ($inTransitAt == null ? 43 : ((Object)$inTransitAt).hashCode());
        LocalDateTime $deliveredAt = this.getDeliveredAt();
        result = result * 59 + ($deliveredAt == null ? 43 : ((Object)$deliveredAt).hashCode());
        LocalDateTime $estimatedDeliveryAt = this.getEstimatedDeliveryAt();
        result = result * 59 + ($estimatedDeliveryAt == null ? 43 : ((Object)$estimatedDeliveryAt).hashCode());
        return result;
    }

    public String toString() {
        return "ConversionOrderDto(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", eccangOrderId=" + this.getEccangOrderId() + ", eccangOrderNumber=" + this.getEccangOrderNumber() + ", orderName=" + this.getOrderName() + ", orderNo=" + this.getOrderNo() + ", longOrderNo=" + this.getLongOrderNo() + ", storeId=" + this.getStoreId() + ", storeName=" + this.getStoreName() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", discountCode=" + this.getDiscountCode() + ", totalPrice=" + this.getTotalPrice() + ", totalShipping=" + this.getTotalShipping() + ", totalRefund=" + this.getTotalRefund() + ", commissionableAmount=" + this.getCommissionableAmount() + ", currency=" + this.getCurrency() + ", commissionRate=" + this.getCommissionRate() + ", commissionAmount=" + this.getCommissionAmount() + ", commissionStatus=" + this.getCommissionStatus() + ", financialStatus=" + this.getFinancialStatus() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", orderCreatedAt=" + this.getOrderCreatedAt() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", customerName=" + this.getCustomerName() + ", customerEmail=" + this.getCustomerEmail() + ", shippingAddress=" + this.getShippingAddress() + ", shippingName=" + this.getShippingName() + ", shippingPhone=" + this.getShippingPhone() + ", shippingCountry=" + this.getShippingCountry() + ", recipientName=" + this.getRecipientName() + ", recipientPhone=" + this.getRecipientPhone() + ", recipientAddress=" + this.getRecipientAddress() + ", recipientCountry=" + this.getRecipientCountry() + ", products=" + this.getProducts() + ", packageNo=" + this.getPackageNo() + ", fulfillmentIds=" + this.getFulfillmentIds() + ", isSplit=" + this.getIsSplit() + ", fulfillmentDisplayStatus=" + this.getFulfillmentDisplayStatus() + ", trackingCompany=" + this.getTrackingCompany() + ", trackingNumber=" + this.getTrackingNumber() + ", trackingUrl=" + this.getTrackingUrl() + ", trackingEventsJson=" + this.getTrackingEventsJson() + ", cancelledAt=" + this.getCancelledAt() + ", closedAt=" + this.getClosedAt() + ", processedAtEccang=" + this.getProcessedAtEccang() + ", fulfillmentCreatedAt=" + this.getFulfillmentCreatedAt() + ", inTransitAt=" + this.getInTransitAt() + ", deliveredAt=" + this.getDeliveredAt() + ", estimatedDeliveryAt=" + this.getEstimatedDeliveryAt() + ")";
    }
}

