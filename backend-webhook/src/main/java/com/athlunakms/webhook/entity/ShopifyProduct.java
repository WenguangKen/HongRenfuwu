package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="shopify_products")
public class ShopifyProduct {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="shopify_product_id", unique=true)
    private Long shopifyProductId;
    @Column(name="store_id")
    private Long storeId;
    @Column(name="title")
    private String title;
    @Column(name="handle")
    private String handle;
    @Column(name="vendor")
    private String vendor;
    @Column(name="product_type")
    private String productType;
    @Column(name="status")
    private String status;
    @Column(name="total_inventory")
    private Integer totalInventory;
    @Column(name="tags", columnDefinition="TEXT")
    private String tags;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="body_html", columnDefinition="TEXT")
    private String bodyHtml;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name="synced_at")
    private LocalDateTime syncedAt;

    public Long getId() {
        return this.id;
    }

    public Long getShopifyProductId() {
        return this.shopifyProductId;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getVendor() {
        return this.vendor;
    }

    public String getProductType() {
        return this.productType;
    }

    public String getStatus() {
        return this.status;
    }

    public Integer getTotalInventory() {
        return this.totalInventory;
    }

    public String getTags() {
        return this.tags;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getBodyHtml() {
        return this.bodyHtml;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getSyncedAt() {
        return this.syncedAt;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalInventory(Integer totalInventory) {
        this.totalInventory = totalInventory;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyProduct)) {
            return false;
        }
        ShopifyProduct other = (ShopifyProduct)o;
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
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        String this$handle = this.getHandle();
        String other$handle = other.getHandle();
        if (this$handle == null ? other$handle != null : !this$handle.equals(other$handle)) {
            return false;
        }
        String this$vendor = this.getVendor();
        String other$vendor = other.getVendor();
        if (this$vendor == null ? other$vendor != null : !this$vendor.equals(other$vendor)) {
            return false;
        }
        String this$productType = this.getProductType();
        String other$productType = other.getProductType();
        if (this$productType == null ? other$productType != null : !this$productType.equals(other$productType)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$tags = this.getTags();
        String other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) {
            return false;
        }
        String this$imageUrl = this.getImageUrl();
        String other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) {
            return false;
        }
        String this$bodyHtml = this.getBodyHtml();
        String other$bodyHtml = other.getBodyHtml();
        if (this$bodyHtml == null ? other$bodyHtml != null : !this$bodyHtml.equals(other$bodyHtml)) {
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
        LocalDateTime this$syncedAt = this.getSyncedAt();
        LocalDateTime other$syncedAt = other.getSyncedAt();
        return !(this$syncedAt == null ? other$syncedAt != null : !((Object)this$syncedAt).equals(other$syncedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyProduct;
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
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $handle = this.getHandle();
        result = result * 59 + ($handle == null ? 43 : $handle.hashCode());
        String $vendor = this.getVendor();
        result = result * 59 + ($vendor == null ? 43 : $vendor.hashCode());
        String $productType = this.getProductType();
        result = result * 59 + ($productType == null ? 43 : $productType.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
        String $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        String $bodyHtml = this.getBodyHtml();
        result = result * 59 + ($bodyHtml == null ? 43 : $bodyHtml.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        LocalDateTime $syncedAt = this.getSyncedAt();
        result = result * 59 + ($syncedAt == null ? 43 : ((Object)$syncedAt).hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyProduct(id=" + this.getId() + ", shopifyProductId=" + this.getShopifyProductId() + ", storeId=" + this.getStoreId() + ", title=" + this.getTitle() + ", handle=" + this.getHandle() + ", vendor=" + this.getVendor() + ", productType=" + this.getProductType() + ", status=" + this.getStatus() + ", totalInventory=" + this.getTotalInventory() + ", tags=" + this.getTags() + ", imageUrl=" + this.getImageUrl() + ", bodyHtml=" + this.getBodyHtml() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", syncedAt=" + this.getSyncedAt() + ")";
    }
}

