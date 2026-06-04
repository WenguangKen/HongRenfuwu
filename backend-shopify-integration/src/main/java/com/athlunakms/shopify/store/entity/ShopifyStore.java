package com.athlunakms.shopify.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="shopify_stores")
public class ShopifyStore {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="store_name", nullable=false)
    private String storeName;
    @Column(name="store_url_encrypted", nullable=false)
    private String storeUrlEncrypted;
    @Column(name="access_token_encrypted", nullable=false)
    private String accessTokenEncrypted;
    @Column(name="notes")
    private String notes;
    @Column(name="status")
    private String status;
    @Column(name="connection_verified")
    private Boolean connectionVerified = false;
    @Column(name="last_verified_at")
    private LocalDateTime lastVerifiedAt;
    @Column(name="shopify_shop_id")
    private Long shopifyShopId;
    @Column(name="shop_domain")
    private String shopDomain;
    @Column(name="myshopify_domain")
    private String myshopifyDomain;
    @Column(name="shop_email_encrypted")
    private String shopEmailEncrypted;
    @Column(name="plan_name")
    private String planName;
    @Column(name="currency")
    private String currency;
    @Column(name="timezone")
    private String timezone;
    @Column(name="country_code")
    private String countryCode;
    @Column(name="created_by")
    private Long createdBy;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public String getStoreUrl() {
        return null;
    }

    public Long getId() {
        return this.id;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public String getStoreUrlEncrypted() {
        return this.storeUrlEncrypted;
    }

    public String getAccessTokenEncrypted() {
        return this.accessTokenEncrypted;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getStatus() {
        return this.status;
    }

    public Boolean getConnectionVerified() {
        return this.connectionVerified;
    }

    public LocalDateTime getLastVerifiedAt() {
        return this.lastVerifiedAt;
    }

    public Long getShopifyShopId() {
        return this.shopifyShopId;
    }

    public String getShopDomain() {
        return this.shopDomain;
    }

    public String getMyshopifyDomain() {
        return this.myshopifyDomain;
    }

    public String getShopEmailEncrypted() {
        return this.shopEmailEncrypted;
    }

    public String getPlanName() {
        return this.planName;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Long getCreatedBy() {
        return this.createdBy;
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

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreUrlEncrypted(String storeUrlEncrypted) {
        this.storeUrlEncrypted = storeUrlEncrypted;
    }

    public void setAccessTokenEncrypted(String accessTokenEncrypted) {
        this.accessTokenEncrypted = accessTokenEncrypted;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setConnectionVerified(Boolean connectionVerified) {
        this.connectionVerified = connectionVerified;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public void setShopifyShopId(Long shopifyShopId) {
        this.shopifyShopId = shopifyShopId;
    }

    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain;
    }

    public void setMyshopifyDomain(String myshopifyDomain) {
        this.myshopifyDomain = myshopifyDomain;
    }

    public void setShopEmailEncrypted(String shopEmailEncrypted) {
        this.shopEmailEncrypted = shopEmailEncrypted;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
        if (!(o instanceof ShopifyStore)) {
            return false;
        }
        ShopifyStore other = (ShopifyStore)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Boolean this$connectionVerified = this.getConnectionVerified();
        Boolean other$connectionVerified = other.getConnectionVerified();
        if (this$connectionVerified == null ? other$connectionVerified != null : !((Object)this$connectionVerified).equals(other$connectionVerified)) {
            return false;
        }
        Long this$shopifyShopId = this.getShopifyShopId();
        Long other$shopifyShopId = other.getShopifyShopId();
        if (this$shopifyShopId == null ? other$shopifyShopId != null : !((Object)this$shopifyShopId).equals(other$shopifyShopId)) {
            return false;
        }
        Long this$createdBy = this.getCreatedBy();
        Long other$createdBy = other.getCreatedBy();
        if (this$createdBy == null ? other$createdBy != null : !((Object)this$createdBy).equals(other$createdBy)) {
            return false;
        }
        String this$storeName = this.getStoreName();
        String other$storeName = other.getStoreName();
        if (this$storeName == null ? other$storeName != null : !this$storeName.equals(other$storeName)) {
            return false;
        }
        String this$storeUrlEncrypted = this.getStoreUrlEncrypted();
        String other$storeUrlEncrypted = other.getStoreUrlEncrypted();
        if (this$storeUrlEncrypted == null ? other$storeUrlEncrypted != null : !this$storeUrlEncrypted.equals(other$storeUrlEncrypted)) {
            return false;
        }
        String this$accessTokenEncrypted = this.getAccessTokenEncrypted();
        String other$accessTokenEncrypted = other.getAccessTokenEncrypted();
        if (this$accessTokenEncrypted == null ? other$accessTokenEncrypted != null : !this$accessTokenEncrypted.equals(other$accessTokenEncrypted)) {
            return false;
        }
        String this$notes = this.getNotes();
        String other$notes = other.getNotes();
        if (this$notes == null ? other$notes != null : !this$notes.equals(other$notes)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        LocalDateTime this$lastVerifiedAt = this.getLastVerifiedAt();
        LocalDateTime other$lastVerifiedAt = other.getLastVerifiedAt();
        if (this$lastVerifiedAt == null ? other$lastVerifiedAt != null : !((Object)this$lastVerifiedAt).equals(other$lastVerifiedAt)) {
            return false;
        }
        String this$shopDomain = this.getShopDomain();
        String other$shopDomain = other.getShopDomain();
        if (this$shopDomain == null ? other$shopDomain != null : !this$shopDomain.equals(other$shopDomain)) {
            return false;
        }
        String this$myshopifyDomain = this.getMyshopifyDomain();
        String other$myshopifyDomain = other.getMyshopifyDomain();
        if (this$myshopifyDomain == null ? other$myshopifyDomain != null : !this$myshopifyDomain.equals(other$myshopifyDomain)) {
            return false;
        }
        String this$shopEmailEncrypted = this.getShopEmailEncrypted();
        String other$shopEmailEncrypted = other.getShopEmailEncrypted();
        if (this$shopEmailEncrypted == null ? other$shopEmailEncrypted != null : !this$shopEmailEncrypted.equals(other$shopEmailEncrypted)) {
            return false;
        }
        String this$planName = this.getPlanName();
        String other$planName = other.getPlanName();
        if (this$planName == null ? other$planName != null : !this$planName.equals(other$planName)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$timezone = this.getTimezone();
        String other$timezone = other.getTimezone();
        if (this$timezone == null ? other$timezone != null : !this$timezone.equals(other$timezone)) {
            return false;
        }
        String this$countryCode = this.getCountryCode();
        String other$countryCode = other.getCountryCode();
        if (this$countryCode == null ? other$countryCode != null : !this$countryCode.equals(other$countryCode)) {
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
        return other instanceof ShopifyStore;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Boolean $connectionVerified = this.getConnectionVerified();
        result = result * 59 + ($connectionVerified == null ? 43 : ((Object)$connectionVerified).hashCode());
        Long $shopifyShopId = this.getShopifyShopId();
        result = result * 59 + ($shopifyShopId == null ? 43 : ((Object)$shopifyShopId).hashCode());
        Long $createdBy = this.getCreatedBy();
        result = result * 59 + ($createdBy == null ? 43 : ((Object)$createdBy).hashCode());
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
        String $storeUrlEncrypted = this.getStoreUrlEncrypted();
        result = result * 59 + ($storeUrlEncrypted == null ? 43 : $storeUrlEncrypted.hashCode());
        String $accessTokenEncrypted = this.getAccessTokenEncrypted();
        result = result * 59 + ($accessTokenEncrypted == null ? 43 : $accessTokenEncrypted.hashCode());
        String $notes = this.getNotes();
        result = result * 59 + ($notes == null ? 43 : $notes.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $lastVerifiedAt = this.getLastVerifiedAt();
        result = result * 59 + ($lastVerifiedAt == null ? 43 : ((Object)$lastVerifiedAt).hashCode());
        String $shopDomain = this.getShopDomain();
        result = result * 59 + ($shopDomain == null ? 43 : $shopDomain.hashCode());
        String $myshopifyDomain = this.getMyshopifyDomain();
        result = result * 59 + ($myshopifyDomain == null ? 43 : $myshopifyDomain.hashCode());
        String $shopEmailEncrypted = this.getShopEmailEncrypted();
        result = result * 59 + ($shopEmailEncrypted == null ? 43 : $shopEmailEncrypted.hashCode());
        String $planName = this.getPlanName();
        result = result * 59 + ($planName == null ? 43 : $planName.hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $timezone = this.getTimezone();
        result = result * 59 + ($timezone == null ? 43 : $timezone.hashCode());
        String $countryCode = this.getCountryCode();
        result = result * 59 + ($countryCode == null ? 43 : $countryCode.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyStore(id=" + this.getId() + ", storeName=" + this.getStoreName() + ", storeUrlEncrypted=" + this.getStoreUrlEncrypted() + ", accessTokenEncrypted=" + this.getAccessTokenEncrypted() + ", notes=" + this.getNotes() + ", status=" + this.getStatus() + ", connectionVerified=" + this.getConnectionVerified() + ", lastVerifiedAt=" + this.getLastVerifiedAt() + ", shopifyShopId=" + this.getShopifyShopId() + ", shopDomain=" + this.getShopDomain() + ", myshopifyDomain=" + this.getMyshopifyDomain() + ", shopEmailEncrypted=" + this.getShopEmailEncrypted() + ", planName=" + this.getPlanName() + ", currency=" + this.getCurrency() + ", timezone=" + this.getTimezone() + ", countryCode=" + this.getCountryCode() + ", createdBy=" + this.getCreatedBy() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

