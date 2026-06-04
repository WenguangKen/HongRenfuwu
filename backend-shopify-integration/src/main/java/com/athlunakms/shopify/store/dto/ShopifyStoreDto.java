package com.athlunakms.shopify.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class ShopifyStoreDto {
    private Long id;
    private String storeName;
    private String storeUrl;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String accessToken;
    private String maskedAccessToken;
    private String notes;
    private String status;
    private Boolean connectionVerified;
    private LocalDateTime lastVerifiedAt;
    private Long shopifyShopId;
    @JsonIgnore
    private String shopDomain;
    private String shopEmail;
    private String planName;
    private String currency;
    private String timezone;
    private String countryCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public String getStoreUrl() {
        return this.storeUrl;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getMaskedAccessToken() {
        return this.maskedAccessToken;
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

    public String getShopEmail() {
        return this.shopEmail;
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

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setMaskedAccessToken(String maskedAccessToken) {
        this.maskedAccessToken = maskedAccessToken;
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

    @JsonIgnore
    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
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
        if (!(o instanceof ShopifyStoreDto)) {
            return false;
        }
        ShopifyStoreDto other = (ShopifyStoreDto)o;
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
        String this$storeName = this.getStoreName();
        String other$storeName = other.getStoreName();
        if (this$storeName == null ? other$storeName != null : !this$storeName.equals(other$storeName)) {
            return false;
        }
        String this$storeUrl = this.getStoreUrl();
        String other$storeUrl = other.getStoreUrl();
        if (this$storeUrl == null ? other$storeUrl != null : !this$storeUrl.equals(other$storeUrl)) {
            return false;
        }
        String this$accessToken = this.getAccessToken();
        String other$accessToken = other.getAccessToken();
        if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
            return false;
        }
        String this$maskedAccessToken = this.getMaskedAccessToken();
        String other$maskedAccessToken = other.getMaskedAccessToken();
        if (this$maskedAccessToken == null ? other$maskedAccessToken != null : !this$maskedAccessToken.equals(other$maskedAccessToken)) {
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
        String this$shopEmail = this.getShopEmail();
        String other$shopEmail = other.getShopEmail();
        if (this$shopEmail == null ? other$shopEmail != null : !this$shopEmail.equals(other$shopEmail)) {
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
        return other instanceof ShopifyStoreDto;
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
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
        String $storeUrl = this.getStoreUrl();
        result = result * 59 + ($storeUrl == null ? 43 : $storeUrl.hashCode());
        String $accessToken = this.getAccessToken();
        result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
        String $maskedAccessToken = this.getMaskedAccessToken();
        result = result * 59 + ($maskedAccessToken == null ? 43 : $maskedAccessToken.hashCode());
        String $notes = this.getNotes();
        result = result * 59 + ($notes == null ? 43 : $notes.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $lastVerifiedAt = this.getLastVerifiedAt();
        result = result * 59 + ($lastVerifiedAt == null ? 43 : ((Object)$lastVerifiedAt).hashCode());
        String $shopDomain = this.getShopDomain();
        result = result * 59 + ($shopDomain == null ? 43 : $shopDomain.hashCode());
        String $shopEmail = this.getShopEmail();
        result = result * 59 + ($shopEmail == null ? 43 : $shopEmail.hashCode());
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
        return "ShopifyStoreDto(id=" + this.getId() + ", storeName=" + this.getStoreName() + ", storeUrl=" + this.getStoreUrl() + ", accessToken=" + this.getAccessToken() + ", maskedAccessToken=" + this.getMaskedAccessToken() + ", notes=" + this.getNotes() + ", status=" + this.getStatus() + ", connectionVerified=" + this.getConnectionVerified() + ", lastVerifiedAt=" + this.getLastVerifiedAt() + ", shopifyShopId=" + this.getShopifyShopId() + ", shopDomain=" + this.getShopDomain() + ", shopEmail=" + this.getShopEmail() + ", planName=" + this.getPlanName() + ", currency=" + this.getCurrency() + ", timezone=" + this.getTimezone() + ", countryCode=" + this.getCountryCode() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

