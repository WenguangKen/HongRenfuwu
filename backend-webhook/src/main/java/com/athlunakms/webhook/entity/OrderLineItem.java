package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name="order_line_items")
public class OrderLineItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id", nullable=false)
    private Long orderId;
    @Column(name="shopify_line_item_id", nullable=false, unique=true, length=100)
    private String shopifyLineItemId;
    @Column(name="shopify_product_id")
    private Long shopifyProductId;
    @Column(name="shopify_variant_id")
    private Long shopifyVariantId;
    @Column(length=100)
    private String sku;
    @Column(length=500)
    private String title;
    @Column(name="variant_title", length=255)
    private String variantTitle;
    @Column(nullable=false)
    private Integer quantity = 1;
    @Column(precision=12, scale=2)
    private BigDecimal price;
    @Column(precision=12, scale=2)
    private BigDecimal total;
    @Column(name="total_discount", precision=12, scale=2)
    private BigDecimal totalDiscount;
    @Column(name="fulfillment_status", length=50)
    private String fulfillmentStatus;
    @Column(name="image_url", columnDefinition="TEXT")
    private String imageUrl;
    @Column(name="fulfillment_id", length=100)
    private String fulfillmentId;
    @Column(name="returned_quantity")
    private Integer returnedQuantity = 0;
    @Column(name="is_returned")
    private Boolean isReturned = false;

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

    public String getFulfillmentStatus() {
        return this.fulfillmentStatus;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getFulfillmentId() {
        return this.fulfillmentId;
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

    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFulfillmentId(String fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
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
        if (!(o instanceof OrderLineItem)) {
            return false;
        }
        OrderLineItem other = (OrderLineItem)o;
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
        String this$imageUrl = this.getImageUrl();
        String other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) {
            return false;
        }
        String this$fulfillmentId = this.getFulfillmentId();
        String other$fulfillmentId = other.getFulfillmentId();
        return !(this$fulfillmentId == null ? other$fulfillmentId != null : !this$fulfillmentId.equals(other$fulfillmentId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderLineItem;
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
        String $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        String $fulfillmentId = this.getFulfillmentId();
        result = result * 59 + ($fulfillmentId == null ? 43 : $fulfillmentId.hashCode());
        return result;
    }

    public String toString() {
        return "OrderLineItem(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyLineItemId=" + this.getShopifyLineItemId() + ", shopifyProductId=" + this.getShopifyProductId() + ", shopifyVariantId=" + this.getShopifyVariantId() + ", sku=" + this.getSku() + ", title=" + this.getTitle() + ", variantTitle=" + this.getVariantTitle() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ", total=" + this.getTotal() + ", totalDiscount=" + this.getTotalDiscount() + ", fulfillmentStatus=" + this.getFulfillmentStatus() + ", imageUrl=" + this.getImageUrl() + ", fulfillmentId=" + this.getFulfillmentId() + ", returnedQuantity=" + this.getReturnedQuantity() + ", isReturned=" + this.getIsReturned() + ")";
    }
}

