package com.athlunakms.shopify.product.dto;

import com.athlunakms.shopify.product.dto.ShopifyVariantDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShopifyProductDto {
    private Long id;
    private Long shopifyProductId;
    private Long storeId;
    private String storeName;
    private String title;
    private String bodyHtml;
    private String productType;
    private String handle;
    private String status;
    private String currency;
    private Integer totalInventory;
    private String spu;
    private String mainImage;
    private String spuImages;
    private String optionsJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime syncAt;
    private String onlineStoreUrl;
    private List<ShopifyVariantDto> variants;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public Long getId() {
        return this.id;
    }

    public Long getShopifyProductId() {
        return this.shopifyProductId;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public String getTitle() {
        return this.title;
    }

    public String getBodyHtml() {
        return this.bodyHtml;
    }

    public String getProductType() {
        return this.productType;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getStatus() {
        return this.status;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Integer getTotalInventory() {
        return this.totalInventory;
    }

    public String getSpu() {
        return this.spu;
    }

    public String getMainImage() {
        return this.mainImage;
    }

    public String getSpuImages() {
        return this.spuImages;
    }

    public String getOptionsJson() {
        return this.optionsJson;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return this.publishedAt;
    }

    public LocalDateTime getSyncAt() {
        return this.syncAt;
    }

    public String getOnlineStoreUrl() {
        return this.onlineStoreUrl;
    }

    public List<ShopifyVariantDto> getVariants() {
        return this.variants;
    }

    public BigDecimal getMinPrice() {
        return this.minPrice;
    }

    public BigDecimal getMaxPrice() {
        return this.maxPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopifyProductId(Long shopifyProductId) {
        this.shopifyProductId = shopifyProductId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTotalInventory(Integer totalInventory) {
        this.totalInventory = totalInventory;
    }

    public void setSpu(String spu) {
        this.spu = spu;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public void setSpuImages(String spuImages) {
        this.spuImages = spuImages;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setSyncAt(LocalDateTime syncAt) {
        this.syncAt = syncAt;
    }

    public void setOnlineStoreUrl(String onlineStoreUrl) {
        this.onlineStoreUrl = onlineStoreUrl;
    }

    public void setVariants(List<ShopifyVariantDto> variants) {
        this.variants = variants;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyProductDto)) {
            return false;
        }
        ShopifyProductDto other = (ShopifyProductDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$shopifyProductId = this.getShopifyProductId();
        Long other$shopifyProductId = other.getShopifyProductId();
        if (this$shopifyProductId == null ? other$shopifyProductId != null : !((Object)this$shopifyProductId).equals(other$shopifyProductId)) {
            return false;
        }
        Long this$storeId = this.getStoreId();
        Long other$storeId = other.getStoreId();
        if (this$storeId == null ? other$storeId != null : !((Object)this$storeId).equals(other$storeId)) {
            return false;
        }
        Integer this$totalInventory = this.getTotalInventory();
        Integer other$totalInventory = other.getTotalInventory();
        if (this$totalInventory == null ? other$totalInventory != null : !((Object)this$totalInventory).equals(other$totalInventory)) {
            return false;
        }
        String this$storeName = this.getStoreName();
        String other$storeName = other.getStoreName();
        if (this$storeName == null ? other$storeName != null : !this$storeName.equals(other$storeName)) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        String this$bodyHtml = this.getBodyHtml();
        String other$bodyHtml = other.getBodyHtml();
        if (this$bodyHtml == null ? other$bodyHtml != null : !this$bodyHtml.equals(other$bodyHtml)) {
            return false;
        }
        String this$productType = this.getProductType();
        String other$productType = other.getProductType();
        if (this$productType == null ? other$productType != null : !this$productType.equals(other$productType)) {
            return false;
        }
        String this$handle = this.getHandle();
        String other$handle = other.getHandle();
        if (this$handle == null ? other$handle != null : !this$handle.equals(other$handle)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$spu = this.getSpu();
        String other$spu = other.getSpu();
        if (this$spu == null ? other$spu != null : !this$spu.equals(other$spu)) {
            return false;
        }
        String this$mainImage = this.getMainImage();
        String other$mainImage = other.getMainImage();
        if (this$mainImage == null ? other$mainImage != null : !this$mainImage.equals(other$mainImage)) {
            return false;
        }
        String this$spuImages = this.getSpuImages();
        String other$spuImages = other.getSpuImages();
        if (this$spuImages == null ? other$spuImages != null : !this$spuImages.equals(other$spuImages)) {
            return false;
        }
        String this$optionsJson = this.getOptionsJson();
        String other$optionsJson = other.getOptionsJson();
        if (this$optionsJson == null ? other$optionsJson != null : !this$optionsJson.equals(other$optionsJson)) {
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
        LocalDateTime this$publishedAt = this.getPublishedAt();
        LocalDateTime other$publishedAt = other.getPublishedAt();
        if (this$publishedAt == null ? other$publishedAt != null : !((Object)this$publishedAt).equals(other$publishedAt)) {
            return false;
        }
        LocalDateTime this$syncAt = this.getSyncAt();
        LocalDateTime other$syncAt = other.getSyncAt();
        if (this$syncAt == null ? other$syncAt != null : !((Object)this$syncAt).equals(other$syncAt)) {
            return false;
        }
        String this$onlineStoreUrl = this.getOnlineStoreUrl();
        String other$onlineStoreUrl = other.getOnlineStoreUrl();
        if (this$onlineStoreUrl == null ? other$onlineStoreUrl != null : !this$onlineStoreUrl.equals(other$onlineStoreUrl)) {
            return false;
        }
        List this$variants = this.getVariants();
        List other$variants = other.getVariants();
        if (this$variants == null ? other$variants != null : !((Object)this$variants).equals(other$variants)) {
            return false;
        }
        BigDecimal this$minPrice = this.getMinPrice();
        BigDecimal other$minPrice = other.getMinPrice();
        if (this$minPrice == null ? other$minPrice != null : !((Object)this$minPrice).equals(other$minPrice)) {
            return false;
        }
        BigDecimal this$maxPrice = this.getMaxPrice();
        BigDecimal other$maxPrice = other.getMaxPrice();
        return !(this$maxPrice == null ? other$maxPrice != null : !((Object)this$maxPrice).equals(other$maxPrice));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyProductDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $shopifyProductId = this.getShopifyProductId();
        result = result * 59 + ($shopifyProductId == null ? 43 : ((Object)$shopifyProductId).hashCode());
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object)$storeId).hashCode());
        Integer $totalInventory = this.getTotalInventory();
        result = result * 59 + ($totalInventory == null ? 43 : ((Object)$totalInventory).hashCode());
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $bodyHtml = this.getBodyHtml();
        result = result * 59 + ($bodyHtml == null ? 43 : $bodyHtml.hashCode());
        String $productType = this.getProductType();
        result = result * 59 + ($productType == null ? 43 : $productType.hashCode());
        String $handle = this.getHandle();
        result = result * 59 + ($handle == null ? 43 : $handle.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $spu = this.getSpu();
        result = result * 59 + ($spu == null ? 43 : $spu.hashCode());
        String $mainImage = this.getMainImage();
        result = result * 59 + ($mainImage == null ? 43 : $mainImage.hashCode());
        String $spuImages = this.getSpuImages();
        result = result * 59 + ($spuImages == null ? 43 : $spuImages.hashCode());
        String $optionsJson = this.getOptionsJson();
        result = result * 59 + ($optionsJson == null ? 43 : $optionsJson.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        LocalDateTime $publishedAt = this.getPublishedAt();
        result = result * 59 + ($publishedAt == null ? 43 : ((Object)$publishedAt).hashCode());
        LocalDateTime $syncAt = this.getSyncAt();
        result = result * 59 + ($syncAt == null ? 43 : ((Object)$syncAt).hashCode());
        String $onlineStoreUrl = this.getOnlineStoreUrl();
        result = result * 59 + ($onlineStoreUrl == null ? 43 : $onlineStoreUrl.hashCode());
        List $variants = this.getVariants();
        result = result * 59 + ($variants == null ? 43 : ((Object)$variants).hashCode());
        BigDecimal $minPrice = this.getMinPrice();
        result = result * 59 + ($minPrice == null ? 43 : ((Object)$minPrice).hashCode());
        BigDecimal $maxPrice = this.getMaxPrice();
        result = result * 59 + ($maxPrice == null ? 43 : ((Object)$maxPrice).hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyProductDto(id=" + this.getId() + ", shopifyProductId=" + this.getShopifyProductId() + ", storeId=" + this.getStoreId() + ", storeName=" + this.getStoreName() + ", title=" + this.getTitle() + ", bodyHtml=" + this.getBodyHtml() + ", productType=" + this.getProductType() + ", handle=" + this.getHandle() + ", status=" + this.getStatus() + ", currency=" + this.getCurrency() + ", totalInventory=" + this.getTotalInventory() + ", spu=" + this.getSpu() + ", mainImage=" + this.getMainImage() + ", spuImages=" + this.getSpuImages() + ", optionsJson=" + this.getOptionsJson() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", publishedAt=" + this.getPublishedAt() + ", syncAt=" + this.getSyncAt() + ", onlineStoreUrl=" + this.getOnlineStoreUrl() + ", variants=" + this.getVariants() + ", minPrice=" + this.getMinPrice() + ", maxPrice=" + this.getMaxPrice() + ")";
    }
}

