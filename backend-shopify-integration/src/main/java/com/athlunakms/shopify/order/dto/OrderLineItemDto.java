package com.athlunakms.shopify.order.dto;

import java.math.BigDecimal;

public class OrderLineItemDto {
    private Long id;
    private Long orderId;
    private String shopifyLineItemId;
    private Long shopifyProductId;
    private Long shopifyVariantId;
    private String sku;
    private String title;
    private String variantTitle;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;
    private BigDecimal totalDiscount;
    private Boolean requiresShipping;
    private Boolean giftCard;
    private Integer fulfillableQuantity;
    private String fulfillmentStatus;
    private String fulfillmentId;
    private String vendor;
    private String imageUrl;
    private Integer returnedQuantity;
    private Boolean isReturned;

    public Long getId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getShopifyLineItemId() {
        return this.shopifyLineItemId;
    }

    public Long getShopifyProductId() {
        return this.shopifyProductId;
    }

    public Long getShopifyVariantId() {
        return this.shopifyVariantId;
    }

    public String getSku() {
        return this.sku;
    }

    public String getTitle() {
        return this.title;
    }

    public String getVariantTitle() {
        return this.variantTitle;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public BigDecimal getTotalDiscount() {
        return this.totalDiscount;
    }

    public Boolean getRequiresShipping() {
        return this.requiresShipping;
    }

    public Boolean getGiftCard() {
        return this.giftCard;
    }

    public Integer getFulfillableQuantity() {
        return this.fulfillableQuantity;
    }

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
    }

    public String getFulfillmentId() {
        return this.fulfillmentId;
    }

    public String getVendor() {
        return this.vendor;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Integer getReturnedQuantity() {
        return this.returnedQuantity;
    }

    public Boolean getIsReturned() {
        return this.isReturned;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setShopifyLineItemId(String shopifyLineItemId) {
        this.shopifyLineItemId = shopifyLineItemId;
    }

    public void setShopifyProductId(Long shopifyProductId) {
        this.shopifyProductId = shopifyProductId;
    }

    public void setShopifyVariantId(Long shopifyVariantId) {
        this.shopifyVariantId = shopifyVariantId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public void setRequiresShipping(Boolean requiresShipping) {
        this.requiresShipping = requiresShipping;
    }

    public void setGiftCard(Boolean giftCard) {
        this.giftCard = giftCard;
    }

    public void setFulfillableQuantity(Integer fulfillableQuantity) {
        this.fulfillableQuantity = fulfillableQuantity;
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderLineItemDto)) {
            return false;
        }
        OrderLineItemDto other = (OrderLineItemDto)o;
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
        Long this$shopifyProductId = this.getShopifyProductId();
        Long other$shopifyProductId = other.getShopifyProductId();
        if (this$shopifyProductId == null ? other$shopifyProductId != null : !((Object)this$shopifyProductId).equals(other$shopifyProductId)) {
            return false;
        }
        Long this$shopifyVariantId = this.getShopifyVariantId();
        Long other$shopifyVariantId = other.getShopifyVariantId();
        if (this$shopifyVariantId == null ? other$shopifyVariantId != null : !((Object)this$shopifyVariantId).equals(other$shopifyVariantId)) {
            return false;
        }
        Integer this$quantity = this.getQuantity();
        Integer other$quantity = other.getQuantity();
        if (this$quantity == null ? other$quantity != null : !((Object)this$quantity).equals(other$quantity)) {
            return false;
        }
        Boolean this$requiresShipping = this.getRequiresShipping();
        Boolean other$requiresShipping = other.getRequiresShipping();
        if (this$requiresShipping == null ? other$requiresShipping != null : !((Object)this$requiresShipping).equals(other$requiresShipping)) {
            return false;
        }
        Boolean this$giftCard = this.getGiftCard();
        Boolean other$giftCard = other.getGiftCard();
        if (this$giftCard == null ? other$giftCard != null : !((Object)this$giftCard).equals(other$giftCard)) {
            return false;
        }
        Integer this$fulfillableQuantity = this.getFulfillableQuantity();
        Integer other$fulfillableQuantity = other.getFulfillableQuantity();
        if (this$fulfillableQuantity == null ? other$fulfillableQuantity != null : !((Object)this$fulfillableQuantity).equals(other$fulfillableQuantity)) {
            return false;
        }
        Integer this$returnedQuantity = this.getReturnedQuantity();
        Integer other$returnedQuantity = other.getReturnedQuantity();
        if (this$returnedQuantity == null ? other$returnedQuantity != null : !((Object)this$returnedQuantity).equals(other$returnedQuantity)) {
            return false;
        }
        Boolean this$isReturned = this.getIsReturned();
        Boolean other$isReturned = other.getIsReturned();
        if (this$isReturned == null ? other$isReturned != null : !((Object)this$isReturned).equals(other$isReturned)) {
            return false;
        }
        String this$shopifyLineItemId = this.getShopifyLineItemId();
        String other$shopifyLineItemId = other.getShopifyLineItemId();
        if (this$shopifyLineItemId == null ? other$shopifyLineItemId != null : !this$shopifyLineItemId.equals(other$shopifyLineItemId)) {
            return false;
        }
        String this$sku = this.getSku();
        String other$sku = other.getSku();
        if (this$sku == null ? other$sku != null : !this$sku.equals(other$sku)) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        String this$variantTitle = this.getVariantTitle();
        String other$variantTitle = other.getVariantTitle();
        if (this$variantTitle == null ? other$variantTitle != null : !this$variantTitle.equals(other$variantTitle)) {
            return false;
        }
        BigDecimal this$price = this.getPrice();
        BigDecimal other$price = other.getPrice();
        if (this$price == null ? other$price != null : !((Object)this$price).equals(other$price)) {
            return false;
        }
        BigDecimal this$total = this.getTotal();
        BigDecimal other$total = other.getTotal();
        if (this$total == null ? other$total != null : !((Object)this$total).equals(other$total)) {
            return false;
        }
        BigDecimal this$totalDiscount = this.getTotalDiscount();
        BigDecimal other$totalDiscount = other.getTotalDiscount();
        if (this$totalDiscount == null ? other$totalDiscount != null : !((Object)this$totalDiscount).equals(other$totalDiscount)) {
            return false;
        }
        String this$fulfillmentStatus = this.getFulfillmentStatus();
        String other$fulfillmentStatus = other.getFulfillmentStatus();
        if (this$fulfillmentStatus == null ? other$fulfillmentStatus != null : !this$fulfillmentStatus.equals(other$fulfillmentStatus)) {
            return false;
        }
        String this$fulfillmentId = this.getFulfillmentId();
        String other$fulfillmentId = other.getFulfillmentId();
        if (this$fulfillmentId == null ? other$fulfillmentId != null : !this$fulfillmentId.equals(other$fulfillmentId)) {
            return false;
        }
        String this$vendor = this.getVendor();
        String other$vendor = other.getVendor();
        if (this$vendor == null ? other$vendor != null : !this$vendor.equals(other$vendor)) {
            return false;
        }
        String this$imageUrl = this.getImageUrl();
        String other$imageUrl = other.getImageUrl();
        return !(this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderLineItemDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Long $shopifyProductId = this.getShopifyProductId();
        result = result * 59 + ($shopifyProductId == null ? 43 : ((Object)$shopifyProductId).hashCode());
        Long $shopifyVariantId = this.getShopifyVariantId();
        result = result * 59 + ($shopifyVariantId == null ? 43 : ((Object)$shopifyVariantId).hashCode());
        Integer $quantity = this.getQuantity();
        result = result * 59 + ($quantity == null ? 43 : ((Object)$quantity).hashCode());
        Boolean $requiresShipping = this.getRequiresShipping();
        result = result * 59 + ($requiresShipping == null ? 43 : ((Object)$requiresShipping).hashCode());
        Boolean $giftCard = this.getGiftCard();
        result = result * 59 + ($giftCard == null ? 43 : ((Object)$giftCard).hashCode());
        Integer $fulfillableQuantity = this.getFulfillableQuantity();
        result = result * 59 + ($fulfillableQuantity == null ? 43 : ((Object)$fulfillableQuantity).hashCode());
        Integer $returnedQuantity = this.getReturnedQuantity();
        result = result * 59 + ($returnedQuantity == null ? 43 : ((Object)$returnedQuantity).hashCode());
        Boolean $isReturned = this.getIsReturned();
        result = result * 59 + ($isReturned == null ? 43 : ((Object)$isReturned).hashCode());
        String $shopifyLineItemId = this.getShopifyLineItemId();
        result = result * 59 + ($shopifyLineItemId == null ? 43 : $shopifyLineItemId.hashCode());
        String $sku = this.getSku();
        result = result * 59 + ($sku == null ? 43 : $sku.hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $variantTitle = this.getVariantTitle();
        result = result * 59 + ($variantTitle == null ? 43 : $variantTitle.hashCode());
        BigDecimal $price = this.getPrice();
        result = result * 59 + ($price == null ? 43 : ((Object)$price).hashCode());
        BigDecimal $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        BigDecimal $totalDiscount = this.getTotalDiscount();
        result = result * 59 + ($totalDiscount == null ? 43 : ((Object)$totalDiscount).hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        String $fulfillmentId = this.getFulfillmentId();
        result = result * 59 + ($fulfillmentId == null ? 43 : $fulfillmentId.hashCode());
        String $vendor = this.getVendor();
        result = result * 59 + ($vendor == null ? 43 : $vendor.hashCode());
        String $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        return result;
    }

    public String toString() {
        return "OrderLineItemDto(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyLineItemId=" + this.getShopifyLineItemId() + ", shopifyProductId=" + this.getShopifyProductId() + ", shopifyVariantId=" + this.getShopifyVariantId() + ", sku=" + this.getSku() + ", title=" + this.getTitle() + ", variantTitle=" + this.getVariantTitle() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ", total=" + this.getTotal() + ", totalDiscount=" + this.getTotalDiscount() + ", requiresShipping=" + this.getRequiresShipping() + ", giftCard=" + this.getGiftCard() + ", fulfillableQuantity=" + this.getFulfillableQuantity() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", fulfillmentId=" + this.getFulfillmentId() + ", vendor=" + this.getVendor() + ", imageUrl=" + this.getImageUrl() + ", returnedQuantity=" + this.getReturnedQuantity() + ", isReturned=" + this.getIsReturned() + ")";
    }
}

