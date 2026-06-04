package com.athlunakms.shopify.order.dto;

import com.athlunakms.shopify.order.dto.OrderLineItemDto;
import com.athlunakms.shopify.order.dto.OrderRefundDto;
import com.athlunakms.shopify.order.dto.OrderTransactionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShopifyOrderDto {
    private Long id;
    private Long storeId;
    private String storeName;
    @JsonIgnore
    private String shopDomain;
    private String shopifyOrderId;
    private Long shopifyOrderNumber;
    private String name;
    private LocalDateTime createdAtShopify;
    private LocalDateTime processedAtShopify;
    private LocalDateTime updatedAtShopify;
    private LocalDateTime closedAtShopify;
    private LocalDateTime cancelledAtShopify;
    private String financialStatus;
    private String fulfillmentStatus;
    private String cancelReason;
    private Boolean fullyPaid;
    private String currency;
    private BigDecimal subtotalPrice;
    private BigDecimal totalTax;
    private BigDecimal totalDiscounts;
    private BigDecimal totalShippingPrice;
    private BigDecimal totalPrice;
    private String shopifyCustomerId;
    private String customerEmail;
    private String customerFirstName;
    private String customerLastName;
    private String customerPhone;
    private String customerName;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingCity;
    private String shippingProvince;
    private String shippingCountry;
    private String shippingZip;
    private String shippingFullAddress;
    private String sourceName;
    private String shippingMethod;
    private String discountCodes;
    private String tags;
    private String note;
    private List<OrderLineItemDto> lineItems;
    private List<OrderTransactionDto> transactions;
    private List<OrderRefundDto> refunds;
    private BigDecimal totalPaid;
    private BigDecimal totalRefunded;
    private BigDecimal netPayment;

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

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public Long getShopifyOrderNumber() {
        return this.shopifyOrderNumber;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getCreatedAtShopify() {
        return this.createdAtShopify;
    }

    public LocalDateTime getProcessedAtShopify() {
        return this.processedAtShopify;
    }

    public LocalDateTime getUpdatedAtShopify() {
        return this.updatedAtShopify;
    }

    public LocalDateTime getClosedAtShopify() {
        return this.closedAtShopify;
    }

    public LocalDateTime getCancelledAtShopify() {
        return this.cancelledAtShopify;
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

    public String getShopifyCustomerId() {
        return this.shopifyCustomerId;
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

    public String getCustomerName() {
        return this.customerName;
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

    public String getShippingCountry() {
        return this.shippingCountry;
    }

    public String getShippingZip() {
        return this.shippingZip;
    }

    public String getShippingFullAddress() {
        return this.shippingFullAddress;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public String getShippingMethod() {
        return this.shippingMethod;
    }

    public String getDiscountCodes() {
        return this.discountCodes;
    }

    public String getTags() {
        return this.tags;
    }

    public String getNote() {
        return this.note;
    }

    public List<OrderLineItemDto> getLineItems() {
        return this.lineItems;
    }

    public List<OrderTransactionDto> getTransactions() {
        return this.transactions;
    }

    public List<OrderRefundDto> getRefunds() {
        return this.refunds;
    }

    public BigDecimal getTotalPaid() {
        return this.totalPaid;
    }

    public BigDecimal getTotalRefunded() {
        return this.totalRefunded;
    }

    public BigDecimal getNetPayment() {
        return this.netPayment;
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

    @JsonIgnore
    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain;
    }

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setShopifyOrderNumber(Long shopifyOrderNumber) {
        this.shopifyOrderNumber = shopifyOrderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAtShopify(LocalDateTime createdAtShopify) {
        this.createdAtShopify = createdAtShopify;
    }

    public void setProcessedAtShopify(LocalDateTime processedAtShopify) {
        this.processedAtShopify = processedAtShopify;
    }

    public void setUpdatedAtShopify(LocalDateTime updatedAtShopify) {
        this.updatedAtShopify = updatedAtShopify;
    }

    public void setClosedAtShopify(LocalDateTime closedAtShopify) {
        this.closedAtShopify = closedAtShopify;
    }

    public void setCancelledAtShopify(LocalDateTime cancelledAtShopify) {
        this.cancelledAtShopify = cancelledAtShopify;
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

    public void setShopifyCustomerId(String shopifyCustomerId) {
        this.shopifyCustomerId = shopifyCustomerId;
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

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }

    public void setShippingFullAddress(String shippingFullAddress) {
        this.shippingFullAddress = shippingFullAddress;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public void setDiscountCodes(String discountCodes) {
        this.discountCodes = discountCodes;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setLineItems(List<OrderLineItemDto> lineItems) {
        this.lineItems = lineItems;
    }

    public void setTransactions(List<OrderTransactionDto> transactions) {
        this.transactions = transactions;
    }

    public void setRefunds(List<OrderRefundDto> refunds) {
        this.refunds = refunds;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public void setTotalRefunded(BigDecimal totalRefunded) {
        this.totalRefunded = totalRefunded;
    }

    public void setNetPayment(BigDecimal netPayment) {
        this.netPayment = netPayment;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyOrderDto)) {
            return false;
        }
        ShopifyOrderDto other = (ShopifyOrderDto)o;
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
        Long this$shopifyOrderNumber = this.getShopifyOrderNumber();
        Long other$shopifyOrderNumber = other.getShopifyOrderNumber();
        if (this$shopifyOrderNumber == null ? other$shopifyOrderNumber != null : !((Object)this$shopifyOrderNumber).equals(other$shopifyOrderNumber)) {
            return false;
        }
        Boolean this$fullyPaid = this.getFullyPaid();
        Boolean other$fullyPaid = other.getFullyPaid();
        if (this$fullyPaid == null ? other$fullyPaid != null : !((Object)this$fullyPaid).equals(other$fullyPaid)) {
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
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        LocalDateTime this$createdAtShopify = this.getCreatedAtShopify();
        LocalDateTime other$createdAtShopify = other.getCreatedAtShopify();
        if (this$createdAtShopify == null ? other$createdAtShopify != null : !((Object)this$createdAtShopify).equals(other$createdAtShopify)) {
            return false;
        }
        LocalDateTime this$processedAtShopify = this.getProcessedAtShopify();
        LocalDateTime other$processedAtShopify = other.getProcessedAtShopify();
        if (this$processedAtShopify == null ? other$processedAtShopify != null : !((Object)this$processedAtShopify).equals(other$processedAtShopify)) {
            return false;
        }
        LocalDateTime this$updatedAtShopify = this.getUpdatedAtShopify();
        LocalDateTime other$updatedAtShopify = other.getUpdatedAtShopify();
        if (this$updatedAtShopify == null ? other$updatedAtShopify != null : !((Object)this$updatedAtShopify).equals(other$updatedAtShopify)) {
            return false;
        }
        LocalDateTime this$closedAtShopify = this.getClosedAtShopify();
        LocalDateTime other$closedAtShopify = other.getClosedAtShopify();
        if (this$closedAtShopify == null ? other$closedAtShopify != null : !((Object)this$closedAtShopify).equals(other$closedAtShopify)) {
            return false;
        }
        LocalDateTime this$cancelledAtShopify = this.getCancelledAtShopify();
        LocalDateTime other$cancelledAtShopify = other.getCancelledAtShopify();
        if (this$cancelledAtShopify == null ? other$cancelledAtShopify != null : !((Object)this$cancelledAtShopify).equals(other$cancelledAtShopify)) {
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
        String this$shopifyCustomerId = this.getShopifyCustomerId();
        String other$shopifyCustomerId = other.getShopifyCustomerId();
        if (this$shopifyCustomerId == null ? other$shopifyCustomerId != null : !this$shopifyCustomerId.equals(other$shopifyCustomerId)) {
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
        String this$customerName = this.getCustomerName();
        String other$customerName = other.getCustomerName();
        if (this$customerName == null ? other$customerName != null : !this$customerName.equals(other$customerName)) {
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
        String this$shippingCountry = this.getShippingCountry();
        String other$shippingCountry = other.getShippingCountry();
        if (this$shippingCountry == null ? other$shippingCountry != null : !this$shippingCountry.equals(other$shippingCountry)) {
            return false;
        }
        String this$shippingZip = this.getShippingZip();
        String other$shippingZip = other.getShippingZip();
        if (this$shippingZip == null ? other$shippingZip != null : !this$shippingZip.equals(other$shippingZip)) {
            return false;
        }
        String this$shippingFullAddress = this.getShippingFullAddress();
        String other$shippingFullAddress = other.getShippingFullAddress();
        if (this$shippingFullAddress == null ? other$shippingFullAddress != null : !this$shippingFullAddress.equals(other$shippingFullAddress)) {
            return false;
        }
        String this$sourceName = this.getSourceName();
        String other$sourceName = other.getSourceName();
        if (this$sourceName == null ? other$sourceName != null : !this$sourceName.equals(other$sourceName)) {
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
        List this$lineItems = this.getLineItems();
        List other$lineItems = other.getLineItems();
        if (this$lineItems == null ? other$lineItems != null : !((Object)this$lineItems).equals(other$lineItems)) {
            return false;
        }
        List this$transactions = this.getTransactions();
        List other$transactions = other.getTransactions();
        if (this$transactions == null ? other$transactions != null : !((Object)this$transactions).equals(other$transactions)) {
            return false;
        }
        List this$refunds = this.getRefunds();
        List other$refunds = other.getRefunds();
        if (this$refunds == null ? other$refunds != null : !((Object)this$refunds).equals(other$refunds)) {
            return false;
        }
        BigDecimal this$totalPaid = this.getTotalPaid();
        BigDecimal other$totalPaid = other.getTotalPaid();
        if (this$totalPaid == null ? other$totalPaid != null : !((Object)this$totalPaid).equals(other$totalPaid)) {
            return false;
        }
        BigDecimal this$totalRefunded = this.getTotalRefunded();
        BigDecimal other$totalRefunded = other.getTotalRefunded();
        if (this$totalRefunded == null ? other$totalRefunded != null : !((Object)this$totalRefunded).equals(other$totalRefunded)) {
            return false;
        }
        BigDecimal this$netPayment = this.getNetPayment();
        BigDecimal other$netPayment = other.getNetPayment();
        return !(this$netPayment == null ? other$netPayment != null : !((Object)this$netPayment).equals(other$netPayment));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyOrderDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object)$storeId).hashCode());
        Long $shopifyOrderNumber = this.getShopifyOrderNumber();
        result = result * 59 + ($shopifyOrderNumber == null ? 43 : ((Object)$shopifyOrderNumber).hashCode());
        Boolean $fullyPaid = this.getFullyPaid();
        result = result * 59 + ($fullyPaid == null ? 43 : ((Object)$fullyPaid).hashCode());
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
        String $shopDomain = this.getShopDomain();
        result = result * 59 + ($shopDomain == null ? 43 : $shopDomain.hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        LocalDateTime $createdAtShopify = this.getCreatedAtShopify();
        result = result * 59 + ($createdAtShopify == null ? 43 : ((Object)$createdAtShopify).hashCode());
        LocalDateTime $processedAtShopify = this.getProcessedAtShopify();
        result = result * 59 + ($processedAtShopify == null ? 43 : ((Object)$processedAtShopify).hashCode());
        LocalDateTime $updatedAtShopify = this.getUpdatedAtShopify();
        result = result * 59 + ($updatedAtShopify == null ? 43 : ((Object)$updatedAtShopify).hashCode());
        LocalDateTime $closedAtShopify = this.getClosedAtShopify();
        result = result * 59 + ($closedAtShopify == null ? 43 : ((Object)$closedAtShopify).hashCode());
        LocalDateTime $cancelledAtShopify = this.getCancelledAtShopify();
        result = result * 59 + ($cancelledAtShopify == null ? 43 : ((Object)$cancelledAtShopify).hashCode());
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
        String $shopifyCustomerId = this.getShopifyCustomerId();
        result = result * 59 + ($shopifyCustomerId == null ? 43 : $shopifyCustomerId.hashCode());
        String $customerEmail = this.getCustomerEmail();
        result = result * 59 + ($customerEmail == null ? 43 : $customerEmail.hashCode());
        String $customerFirstName = this.getCustomerFirstName();
        result = result * 59 + ($customerFirstName == null ? 43 : $customerFirstName.hashCode());
        String $customerLastName = this.getCustomerLastName();
        result = result * 59 + ($customerLastName == null ? 43 : $customerLastName.hashCode());
        String $customerPhone = this.getCustomerPhone();
        result = result * 59 + ($customerPhone == null ? 43 : $customerPhone.hashCode());
        String $customerName = this.getCustomerName();
        result = result * 59 + ($customerName == null ? 43 : $customerName.hashCode());
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
        String $shippingCountry = this.getShippingCountry();
        result = result * 59 + ($shippingCountry == null ? 43 : $shippingCountry.hashCode());
        String $shippingZip = this.getShippingZip();
        result = result * 59 + ($shippingZip == null ? 43 : $shippingZip.hashCode());
        String $shippingFullAddress = this.getShippingFullAddress();
        result = result * 59 + ($shippingFullAddress == null ? 43 : $shippingFullAddress.hashCode());
        String $sourceName = this.getSourceName();
        result = result * 59 + ($sourceName == null ? 43 : $sourceName.hashCode());
        String $shippingMethod = this.getShippingMethod();
        result = result * 59 + ($shippingMethod == null ? 43 : $shippingMethod.hashCode());
        String $discountCodes = this.getDiscountCodes();
        result = result * 59 + ($discountCodes == null ? 43 : $discountCodes.hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
        String $note = this.getNote();
        result = result * 59 + ($note == null ? 43 : $note.hashCode());
        List $lineItems = this.getLineItems();
        result = result * 59 + ($lineItems == null ? 43 : ((Object)$lineItems).hashCode());
        List $transactions = this.getTransactions();
        result = result * 59 + ($transactions == null ? 43 : ((Object)$transactions).hashCode());
        List $refunds = this.getRefunds();
        result = result * 59 + ($refunds == null ? 43 : ((Object)$refunds).hashCode());
        BigDecimal $totalPaid = this.getTotalPaid();
        result = result * 59 + ($totalPaid == null ? 43 : ((Object)$totalPaid).hashCode());
        BigDecimal $totalRefunded = this.getTotalRefunded();
        result = result * 59 + ($totalRefunded == null ? 43 : ((Object)$totalRefunded).hashCode());
        BigDecimal $netPayment = this.getNetPayment();
        result = result * 59 + ($netPayment == null ? 43 : ((Object)$netPayment).hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyOrderDto(id=" + this.getId() + ", storeId=" + this.getStoreId() + ", storeName=" + this.getStoreName() + ", shopDomain=" + this.getShopDomain() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber() + ", name=" + this.getName() + ", createdAtShopify=" + this.getCreatedAtShopify() + ", processedAtShopify=" + this.getProcessedAtShopify() + ", updatedAtShopify=" + this.getUpdatedAtShopify() + ", closedAtShopify=" + this.getClosedAtShopify() + ", cancelledAtShopify=" + this.getCancelledAtShopify() + ", financialStatus=" + this.getFinancialStatus() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", cancelReason=" + this.getCancelReason() + ", fullyPaid=" + this.getFullyPaid() + ", currency=" + this.getCurrency() + ", subtotalPrice=" + this.getSubtotalPrice() + ", totalTax=" + this.getTotalTax() + ", totalDiscounts=" + this.getTotalDiscounts() + ", totalShippingPrice=" + this.getTotalShippingPrice() + ", totalPrice=" + this.getTotalPrice() + ", shopifyCustomerId=" + this.getShopifyCustomerId() + ", customerEmail=" + this.getCustomerEmail() + ", customerFirstName=" + this.getCustomerFirstName() + ", customerLastName=" + this.getCustomerLastName() + ", customerPhone=" + this.getCustomerPhone() + ", customerName=" + this.getCustomerName() + ", shippingName=" + this.getShippingName() + ", shippingPhone=" + this.getShippingPhone() + ", shippingAddress1=" + this.getShippingAddress1() + ", shippingAddress2=" + this.getShippingAddress2() + ", shippingCity=" + this.getShippingCity() + ", shippingProvince=" + this.getShippingProvince() + ", shippingCountry=" + this.getShippingCountry() + ", shippingZip=" + this.getShippingZip() + ", shippingFullAddress=" + this.getShippingFullAddress() + ", sourceName=" + this.getSourceName() + ", shippingMethod=" + this.getShippingMethod() + ", discountCodes=" + this.getDiscountCodes() + ", tags=" + this.getTags() + ", note=" + this.getNote() + ", lineItems=" + this.getLineItems() + ", transactions=" + this.getTransactions() + ", refunds=" + this.getRefunds() + ", totalPaid=" + this.getTotalPaid() + ", totalRefunded=" + this.getTotalRefunded() + ", netPayment=" + this.getNetPayment() + ")";
    }
}

