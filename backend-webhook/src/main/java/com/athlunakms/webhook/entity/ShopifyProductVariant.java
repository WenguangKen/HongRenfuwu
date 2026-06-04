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
@Table(name="shopify_product_variants")
public class ShopifyProductVariant {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="shopify_variant_id", unique=true)
    private Long shopifyVariantId;
    @Column(name="shopify_product_id")
    private Long shopifyProductId;
    @Column(name="product_id")
    private Long productId;
    @Column(name="title")
    private String title;
    @Column(name="sku")
    private String sku;
    @Column(name="barcode")
    private String barcode;
    @Column(name="price")
    private BigDecimal price;
    @Column(name="compare_at_price")
    private BigDecimal compareAtPrice;
    @Column(name="inventory_item_id")
    private Long inventoryItemId;
    @Column(name="inventory_quantity")
    private Integer inventoryQuantity;
    @Column(name="weight")
    private BigDecimal weight;
    @Column(name="weight_unit")
    private String weightUnit;
    @Column(name="option1")
    private String option1;
    @Column(name="option2")
    private String option2;
    @Column(name="option3")
    private String option3;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public Long getShopifyVariantId() {
        return this.shopifyVariantId;
    }

    public Long getShopifyProductId() {
        return this.shopifyProductId;
    }

    public Long getProductId() {
        return this.productId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSku() {
        return this.sku;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getCompareAtPrice() {
        return this.compareAtPrice;
    }

    public Long getInventoryItemId() {
        return this.inventoryItemId;
    }

    public Integer getInventoryQuantity() {
        return this.inventoryQuantity;
    }

    public BigDecimal getWeight() {
        return this.weight;
    }

    public String getWeightUnit() {
        return this.weightUnit;
    }

    public String getOption1() {
        return this.option1;
    }

    public String getOption2() {
        return this.option2;
    }

    public String getOption3() {
        return this.option3;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopifyVariantId(Long shopifyVariantId) {
        this.shopifyVariantId = shopifyVariantId;
    }

    public void setShopifyProductId(Long shopifyProductId) {
        this.shopifyProductId = shopifyProductId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public void setInventoryQuantity(Integer inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyProductVariant)) {
            return false;
        }
        ShopifyProductVariant other = (ShopifyProductVariant)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$shopifyVariantId = this.getShopifyVariantId();
        Long other$shopifyVariantId = other.getShopifyVariantId();
        if (this$shopifyVariantId == null ? other$shopifyVariantId != null : !((Object)this$shopifyVariantId).equals(other$shopifyVariantId)) {
            return false;
        }
        Long this$shopifyProductId = this.getShopifyProductId();
        Long other$shopifyProductId = other.getShopifyProductId();
        if (this$shopifyProductId == null ? other$shopifyProductId != null : !((Object)this$shopifyProductId).equals(other$shopifyProductId)) {
            return false;
        }
        Long this$productId = this.getProductId();
        Long other$productId = other.getProductId();
        if (this$productId == null ? other$productId != null : !((Object)this$productId).equals(other$productId)) {
            return false;
        }
        Long this$inventoryItemId = this.getInventoryItemId();
        Long other$inventoryItemId = other.getInventoryItemId();
        if (this$inventoryItemId == null ? other$inventoryItemId != null : !((Object)this$inventoryItemId).equals(other$inventoryItemId)) {
            return false;
        }
        Integer this$inventoryQuantity = this.getInventoryQuantity();
        Integer other$inventoryQuantity = other.getInventoryQuantity();
        if (this$inventoryQuantity == null ? other$inventoryQuantity != null : !((Object)this$inventoryQuantity).equals(other$inventoryQuantity)) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        String this$sku = this.getSku();
        String other$sku = other.getSku();
        if (this$sku == null ? other$sku != null : !this$sku.equals(other$sku)) {
            return false;
        }
        String this$barcode = this.getBarcode();
        String other$barcode = other.getBarcode();
        if (this$barcode == null ? other$barcode != null : !this$barcode.equals(other$barcode)) {
            return false;
        }
        BigDecimal this$price = this.getPrice();
        BigDecimal other$price = other.getPrice();
        if (this$price == null ? other$price != null : !((Object)this$price).equals(other$price)) {
            return false;
        }
        BigDecimal this$compareAtPrice = this.getCompareAtPrice();
        BigDecimal other$compareAtPrice = other.getCompareAtPrice();
        if (this$compareAtPrice == null ? other$compareAtPrice != null : !((Object)this$compareAtPrice).equals(other$compareAtPrice)) {
            return false;
        }
        BigDecimal this$weight = this.getWeight();
        BigDecimal other$weight = other.getWeight();
        if (this$weight == null ? other$weight != null : !((Object)this$weight).equals(other$weight)) {
            return false;
        }
        String this$weightUnit = this.getWeightUnit();
        String other$weightUnit = other.getWeightUnit();
        if (this$weightUnit == null ? other$weightUnit != null : !this$weightUnit.equals(other$weightUnit)) {
            return false;
        }
        String this$option1 = this.getOption1();
        String other$option1 = other.getOption1();
        if (this$option1 == null ? other$option1 != null : !this$option1.equals(other$option1)) {
            return false;
        }
        String this$option2 = this.getOption2();
        String other$option2 = other.getOption2();
        if (this$option2 == null ? other$option2 != null : !this$option2.equals(other$option2)) {
            return false;
        }
        String this$option3 = this.getOption3();
        String other$option3 = other.getOption3();
        if (this$option3 == null ? other$option3 != null : !this$option3.equals(other$option3)) {
            return false;
        }
        String this$imageUrl = this.getImageUrl();
        String other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        return !(this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyProductVariant;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $shopifyVariantId = this.getShopifyVariantId();
        result = result * 59 + ($shopifyVariantId == null ? 43 : ((Object)$shopifyVariantId).hashCode());
        Long $shopifyProductId = this.getShopifyProductId();
        result = result * 59 + ($shopifyProductId == null ? 43 : ((Object)$shopifyProductId).hashCode());
        Long $productId = this.getProductId();
        result = result * 59 + ($productId == null ? 43 : ((Object)$productId).hashCode());
        Long $inventoryItemId = this.getInventoryItemId();
        result = result * 59 + ($inventoryItemId == null ? 43 : ((Object)$inventoryItemId).hashCode());
        Integer $inventoryQuantity = this.getInventoryQuantity();
        result = result * 59 + ($inventoryQuantity == null ? 43 : ((Object)$inventoryQuantity).hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $sku = this.getSku();
        result = result * 59 + ($sku == null ? 43 : $sku.hashCode());
        String $barcode = this.getBarcode();
        result = result * 59 + ($barcode == null ? 43 : $barcode.hashCode());
        BigDecimal $price = this.getPrice();
        result = result * 59 + ($price == null ? 43 : ((Object)$price).hashCode());
        BigDecimal $compareAtPrice = this.getCompareAtPrice();
        result = result * 59 + ($compareAtPrice == null ? 43 : ((Object)$compareAtPrice).hashCode());
        BigDecimal $weight = this.getWeight();
        result = result * 59 + ($weight == null ? 43 : ((Object)$weight).hashCode());
        String $weightUnit = this.getWeightUnit();
        result = result * 59 + ($weightUnit == null ? 43 : $weightUnit.hashCode());
        String $option1 = this.getOption1();
        result = result * 59 + ($option1 == null ? 43 : $option1.hashCode());
        String $option2 = this.getOption2();
        result = result * 59 + ($option2 == null ? 43 : $option2.hashCode());
        String $option3 = this.getOption3();
        result = result * 59 + ($option3 == null ? 43 : $option3.hashCode());
        String $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyProductVariant(id=" + this.getId() + ", shopifyVariantId=" + this.getShopifyVariantId() + ", shopifyProductId=" + this.getShopifyProductId() + ", productId=" + this.getProductId() + ", title=" + this.getTitle() + ", sku=" + this.getSku() + ", barcode=" + this.getBarcode() + ", price=" + this.getPrice() + ", compareAtPrice=" + this.getCompareAtPrice() + ", inventoryItemId=" + this.getInventoryItemId() + ", inventoryQuantity=" + this.getInventoryQuantity() + ", weight=" + this.getWeight() + ", weightUnit=" + this.getWeightUnit() + ", option1=" + this.getOption1() + ", option2=" + this.getOption2() + ", option3=" + this.getOption3() + ", imageUrl=" + this.getImageUrl() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

