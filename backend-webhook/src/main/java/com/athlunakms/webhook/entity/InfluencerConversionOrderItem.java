package com.athlunakms.webhook.entity;

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
@Table(name="influencer_conversion_order_item")
public class InfluencerConversionOrderItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="conversion_order_id", nullable=false)
    private Long conversionOrderId;
    @Column(name="shopify_line_item_id")
    private String shopifyLineItemId;
    @Column(name="shopify_product_id")
    private Long shopifyProductId;
    @Column(name="shopify_variant_id")
    private Long shopifyVariantId;
    @Column(name="sku")
    private String sku;
    @Column(name="title", length=500)
    private String title;
    @Column(name="variant_title")
    private String variantTitle;
    @Column(name="quantity")
    private Integer quantity;
    @Column(name="price")
    private BigDecimal price;
    @Column(name="image_url", columnDefinition="TEXT")
    private String imageUrl;
    @Column(name="commission_amount")
    private BigDecimal commissionAmount;
    @Column(name="returned_quantity")
    private Integer returnedQuantity = 0;
    @Column(name="fulfillment_status", length=50)
    private String fulfillmentStatus;
    @Column(name="fulfillment_id", length=100)
    private String fulfillmentId;
    @Column(name="vendor")
    private String vendor;
    @Column(name="total", precision=12, scale=2)
    private BigDecimal total;
    @Column(name="total_discount", precision=12, scale=2)
    private BigDecimal totalDiscount;
    @Column(name="requires_shipping")
    private Boolean requiresShipping = true;
    @Column(name="gift_card")
    private Boolean giftCard = false;
    @Column(name="fulfillable_quantity")
    private Integer fulfillableQuantity = 0;
    @Column(name="is_returned")
    private Boolean isReturned = false;
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return this.id;
    }

    public Long getConversionOrderId() {
        return this.conversionOrderId;
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

    public String getImageUrl() {
        return this.imageUrl;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public Integer getReturnedQuantity() {
        return this.returnedQuantity;
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

    public Boolean getIsReturned() {
        return this.isReturned;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setConversionOrderId(Long conversionOrderId) {
        this.conversionOrderId = conversionOrderId;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
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

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerConversionOrderItem)) {
            return false;
        }
        InfluencerConversionOrderItem other = (InfluencerConversionOrderItem)o;
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
        Integer this$returnedQuantity = this.getReturnedQuantity();
        Integer other$returnedQuantity = other.getReturnedQuantity();
        if (this$returnedQuantity == null ? other$returnedQuantity != null : !((Object)this$returnedQuantity).equals(other$returnedQuantity)) {
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
        String this$imageUrl = this.getImageUrl();
        String other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) {
            return false;
        }
        BigDecimal this$commissionAmount = this.getCommissionAmount();
        BigDecimal other$commissionAmount = other.getCommissionAmount();
        if (this$commissionAmount == null ? other$commissionAmount != null : !((Object)this$commissionAmount).equals(other$commissionAmount)) {
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
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerConversionOrderItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $conversionOrderId = this.getConversionOrderId();
        result = result * 59 + ($conversionOrderId == null ? 43 : ((Object)$conversionOrderId).hashCode());
        Long $shopifyProductId = this.getShopifyProductId();
        result = result * 59 + ($shopifyProductId == null ? 43 : ((Object)$shopifyProductId).hashCode());
        Long $shopifyVariantId = this.getShopifyVariantId();
        result = result * 59 + ($shopifyVariantId == null ? 43 : ((Object)$shopifyVariantId).hashCode());
        Integer $quantity = this.getQuantity();
        result = result * 59 + ($quantity == null ? 43 : ((Object)$quantity).hashCode());
        Integer $returnedQuantity = this.getReturnedQuantity();
        result = result * 59 + ($returnedQuantity == null ? 43 : ((Object)$returnedQuantity).hashCode());
        Boolean $requiresShipping = this.getRequiresShipping();
        result = result * 59 + ($requiresShipping == null ? 43 : ((Object)$requiresShipping).hashCode());
        Boolean $giftCard = this.getGiftCard();
        result = result * 59 + ($giftCard == null ? 43 : ((Object)$giftCard).hashCode());
        Integer $fulfillableQuantity = this.getFulfillableQuantity();
        result = result * 59 + ($fulfillableQuantity == null ? 43 : ((Object)$fulfillableQuantity).hashCode());
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
        String $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        BigDecimal $commissionAmount = this.getCommissionAmount();
        result = result * 59 + ($commissionAmount == null ? 43 : ((Object)$commissionAmount).hashCode());
        String $fulfillmentStatus = this.getFulfillmentStatus();
        result = result * 59 + ($fulfillmentStatus == null ? 43 : $fulfillmentStatus.hashCode());
        String $fulfillmentId = this.getFulfillmentId();
        result = result * 59 + ($fulfillmentId == null ? 43 : $fulfillmentId.hashCode());
        String $vendor = this.getVendor();
        result = result * 59 + ($vendor == null ? 43 : $vendor.hashCode());
        BigDecimal $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : ((Object)$total).hashCode());
        BigDecimal $totalDiscount = this.getTotalDiscount();
        result = result * 59 + ($totalDiscount == null ? 43 : ((Object)$totalDiscount).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerConversionOrderItem(id=" + this.getId() + ", conversionOrderId=" + this.getConversionOrderId() + ", shopifyLineItemId=" + this.getShopifyLineItemId() + ", shopifyProductId=" + this.getShopifyProductId() + ", shopifyVariantId=" + this.getShopifyVariantId() + ", sku=" + this.getSku() + ", title=" + this.getTitle() + ", variantTitle=" + this.getVariantTitle() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ", imageUrl=" + this.getImageUrl() + ", commissionAmount=" + this.getCommissionAmount() + ", returnedQuantity=" + this.getReturnedQuantity() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", fulfillmentId=" + this.getFulfillmentId() + ", vendor=" + this.getVendor() + ", total=" + this.getTotal() + ", totalDiscount=" + this.getTotalDiscount() + ", requiresShipping=" + this.getRequiresShipping() + ", giftCard=" + this.getGiftCard() + ", fulfillableQuantity=" + this.getFulfillableQuantity() + ", isReturned=" + this.getIsReturned() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

