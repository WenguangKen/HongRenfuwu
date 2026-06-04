package com.athlunakms.shopify.product.dto;

import java.math.BigDecimal;

public class ShopifyVariantDto {
    private Long id;
    private Long productId;
    private Long shopifyVariantId;
    private String title;
    private String sku;
    private String barcode;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer inventoryQuantity;
    private String option1;
    private String option2;
    private String option3;
    private String imageUrl;
    private BigDecimal weight;
    private String weightUnit;
    private Integer position;
    private String spu;
    private String productTitle;

    public Long getId() {
        return this.id;
    }

    public Long getProductId() {
        return this.productId;
    }

    public Long getShopifyVariantId() {
        return this.shopifyVariantId;
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

    public Integer getInventoryQuantity() {
        return this.inventoryQuantity;
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

    public BigDecimal getWeight() {
        return this.weight;
    }

    public String getWeightUnit() {
        return this.weightUnit;
    }

    public Integer getPosition() {
        return this.position;
    }

    public String getSpu() {
        return this.spu;
    }

    public String getProductTitle() {
        return this.productTitle;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setShopifyVariantId(Long shopifyVariantId) {
        this.shopifyVariantId = shopifyVariantId;
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

    public void setInventoryQuantity(Integer inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
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

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setSpu(String spu) {
        this.spu = spu;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyVariantDto)) {
            return false;
        }
        ShopifyVariantDto other = (ShopifyVariantDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$productId = this.getProductId();
        Long other$productId = other.getProductId();
        if (this$productId == null ? other$productId != null : !((Object)this$productId).equals(other$productId)) {
            return false;
        }
        Long this$shopifyVariantId = this.getShopifyVariantId();
        Long other$shopifyVariantId = other.getShopifyVariantId();
        if (this$shopifyVariantId == null ? other$shopifyVariantId != null : !((Object)this$shopifyVariantId).equals(other$shopifyVariantId)) {
            return false;
        }
        Integer this$inventoryQuantity = this.getInventoryQuantity();
        Integer other$inventoryQuantity = other.getInventoryQuantity();
        if (this$inventoryQuantity == null ? other$inventoryQuantity != null : !((Object)this$inventoryQuantity).equals(other$inventoryQuantity)) {
            return false;
        }
        Integer this$position = this.getPosition();
        Integer other$position = other.getPosition();
        if (this$position == null ? other$position != null : !((Object)this$position).equals(other$position)) {
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
        String this$spu = this.getSpu();
        String other$spu = other.getSpu();
        if (this$spu == null ? other$spu != null : !this$spu.equals(other$spu)) {
            return false;
        }
        String this$productTitle = this.getProductTitle();
        String other$productTitle = other.getProductTitle();
        return !(this$productTitle == null ? other$productTitle != null : !this$productTitle.equals(other$productTitle));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyVariantDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $productId = this.getProductId();
        result = result * 59 + ($productId == null ? 43 : ((Object)$productId).hashCode());
        Long $shopifyVariantId = this.getShopifyVariantId();
        result = result * 59 + ($shopifyVariantId == null ? 43 : ((Object)$shopifyVariantId).hashCode());
        Integer $inventoryQuantity = this.getInventoryQuantity();
        result = result * 59 + ($inventoryQuantity == null ? 43 : ((Object)$inventoryQuantity).hashCode());
        Integer $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : ((Object)$position).hashCode());
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
        String $option1 = this.getOption1();
        result = result * 59 + ($option1 == null ? 43 : $option1.hashCode());
        String $option2 = this.getOption2();
        result = result * 59 + ($option2 == null ? 43 : $option2.hashCode());
        String $option3 = this.getOption3();
        result = result * 59 + ($option3 == null ? 43 : $option3.hashCode());
        String $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        BigDecimal $weight = this.getWeight();
        result = result * 59 + ($weight == null ? 43 : ((Object)$weight).hashCode());
        String $weightUnit = this.getWeightUnit();
        result = result * 59 + ($weightUnit == null ? 43 : $weightUnit.hashCode());
        String $spu = this.getSpu();
        result = result * 59 + ($spu == null ? 43 : $spu.hashCode());
        String $productTitle = this.getProductTitle();
        result = result * 59 + ($productTitle == null ? 43 : $productTitle.hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyVariantDto(id=" + this.getId() + ", productId=" + this.getProductId() + ", shopifyVariantId=" + this.getShopifyVariantId() + ", title=" + this.getTitle() + ", sku=" + this.getSku() + ", barcode=" + this.getBarcode() + ", price=" + this.getPrice() + ", compareAtPrice=" + this.getCompareAtPrice() + ", inventoryQuantity=" + this.getInventoryQuantity() + ", option1=" + this.getOption1() + ", option2=" + this.getOption2() + ", option3=" + this.getOption3() + ", imageUrl=" + this.getImageUrl() + ", weight=" + this.getWeight() + ", weightUnit=" + this.getWeightUnit() + ", position=" + this.getPosition() + ", spu=" + this.getSpu() + ", productTitle=" + this.getProductTitle() + ")";
    }
}

