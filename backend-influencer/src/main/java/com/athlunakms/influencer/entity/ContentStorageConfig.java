package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="content_storage_config")
public class ContentStorageConfig {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="storage_type", nullable=false)
    private String storageType = "LOCAL";
    @Column(name="base_path", nullable=false)
    private String basePath;
    @Column(name="public_url_prefix")
    private String publicUrlPrefix;
    @Column(name="access_key")
    private String accessKey;
    @Column(name="secret_key")
    private String secretKey;
    @Column(name="region")
    private String region;
    @Column(name="endpoint")
    private String endpoint;
    @Column(name="cdn_domain")
    private String cdnDomain;
    @Column(name="max_file_size")
    private Long maxFileSize = 0x6400000L;
    @Column(name="allowed_types")
    private String allowedTypes = "image/*,video/*,application/pdf";
    @Column(name="total_quota")
    private Long totalQuota = 0x280000000L;
    @Column(name="current_usage")
    private Long currentUsage = 0L;
    @Column(name="is_active")
    private Boolean isActive = true;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public String getStorageType() {
        return this.storageType;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public String getPublicUrlPrefix() {
        return this.publicUrlPrefix;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public String getRegion() {
        return this.region;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getCdnDomain() {
        return this.cdnDomain;
    }

    public Long getMaxFileSize() {
        return this.maxFileSize;
    }

    public String getAllowedTypes() {
        return this.allowedTypes;
    }

    public Long getTotalQuota() {
        return this.totalQuota;
    }

    public Long getCurrentUsage() {
        return this.currentUsage;
    }

    public Boolean getIsActive() {
        return this.isActive;
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

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setPublicUrlPrefix(String publicUrlPrefix) {
        this.publicUrlPrefix = publicUrlPrefix;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setCdnDomain(String cdnDomain) {
        this.cdnDomain = cdnDomain;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public void setTotalQuota(Long totalQuota) {
        this.totalQuota = totalQuota;
    }

    public void setCurrentUsage(Long currentUsage) {
        this.currentUsage = currentUsage;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        if (!(o instanceof ContentStorageConfig)) {
            return false;
        }
        ContentStorageConfig other = (ContentStorageConfig)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$maxFileSize = this.getMaxFileSize();
        Long other$maxFileSize = other.getMaxFileSize();
        if (this$maxFileSize == null ? other$maxFileSize != null : !((Object)this$maxFileSize).equals(other$maxFileSize)) {
            return false;
        }
        Long this$totalQuota = this.getTotalQuota();
        Long other$totalQuota = other.getTotalQuota();
        if (this$totalQuota == null ? other$totalQuota != null : !((Object)this$totalQuota).equals(other$totalQuota)) {
            return false;
        }
        Long this$currentUsage = this.getCurrentUsage();
        Long other$currentUsage = other.getCurrentUsage();
        if (this$currentUsage == null ? other$currentUsage != null : !((Object)this$currentUsage).equals(other$currentUsage)) {
            return false;
        }
        Boolean this$isActive = this.getIsActive();
        Boolean other$isActive = other.getIsActive();
        if (this$isActive == null ? other$isActive != null : !((Object)this$isActive).equals(other$isActive)) {
            return false;
        }
        String this$storageType = this.getStorageType();
        String other$storageType = other.getStorageType();
        if (this$storageType == null ? other$storageType != null : !this$storageType.equals(other$storageType)) {
            return false;
        }
        String this$basePath = this.getBasePath();
        String other$basePath = other.getBasePath();
        if (this$basePath == null ? other$basePath != null : !this$basePath.equals(other$basePath)) {
            return false;
        }
        String this$publicUrlPrefix = this.getPublicUrlPrefix();
        String other$publicUrlPrefix = other.getPublicUrlPrefix();
        if (this$publicUrlPrefix == null ? other$publicUrlPrefix != null : !this$publicUrlPrefix.equals(other$publicUrlPrefix)) {
            return false;
        }
        String this$accessKey = this.getAccessKey();
        String other$accessKey = other.getAccessKey();
        if (this$accessKey == null ? other$accessKey != null : !this$accessKey.equals(other$accessKey)) {
            return false;
        }
        String this$secretKey = this.getSecretKey();
        String other$secretKey = other.getSecretKey();
        if (this$secretKey == null ? other$secretKey != null : !this$secretKey.equals(other$secretKey)) {
            return false;
        }
        String this$region = this.getRegion();
        String other$region = other.getRegion();
        if (this$region == null ? other$region != null : !this$region.equals(other$region)) {
            return false;
        }
        String this$endpoint = this.getEndpoint();
        String other$endpoint = other.getEndpoint();
        if (this$endpoint == null ? other$endpoint != null : !this$endpoint.equals(other$endpoint)) {
            return false;
        }
        String this$cdnDomain = this.getCdnDomain();
        String other$cdnDomain = other.getCdnDomain();
        if (this$cdnDomain == null ? other$cdnDomain != null : !this$cdnDomain.equals(other$cdnDomain)) {
            return false;
        }
        String this$allowedTypes = this.getAllowedTypes();
        String other$allowedTypes = other.getAllowedTypes();
        if (this$allowedTypes == null ? other$allowedTypes != null : !this$allowedTypes.equals(other$allowedTypes)) {
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
        return other instanceof ContentStorageConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $maxFileSize = this.getMaxFileSize();
        result = result * 59 + ($maxFileSize == null ? 43 : ((Object)$maxFileSize).hashCode());
        Long $totalQuota = this.getTotalQuota();
        result = result * 59 + ($totalQuota == null ? 43 : ((Object)$totalQuota).hashCode());
        Long $currentUsage = this.getCurrentUsage();
        result = result * 59 + ($currentUsage == null ? 43 : ((Object)$currentUsage).hashCode());
        Boolean $isActive = this.getIsActive();
        result = result * 59 + ($isActive == null ? 43 : ((Object)$isActive).hashCode());
        String $storageType = this.getStorageType();
        result = result * 59 + ($storageType == null ? 43 : $storageType.hashCode());
        String $basePath = this.getBasePath();
        result = result * 59 + ($basePath == null ? 43 : $basePath.hashCode());
        String $publicUrlPrefix = this.getPublicUrlPrefix();
        result = result * 59 + ($publicUrlPrefix == null ? 43 : $publicUrlPrefix.hashCode());
        String $accessKey = this.getAccessKey();
        result = result * 59 + ($accessKey == null ? 43 : $accessKey.hashCode());
        String $secretKey = this.getSecretKey();
        result = result * 59 + ($secretKey == null ? 43 : $secretKey.hashCode());
        String $region = this.getRegion();
        result = result * 59 + ($region == null ? 43 : $region.hashCode());
        String $endpoint = this.getEndpoint();
        result = result * 59 + ($endpoint == null ? 43 : $endpoint.hashCode());
        String $cdnDomain = this.getCdnDomain();
        result = result * 59 + ($cdnDomain == null ? 43 : $cdnDomain.hashCode());
        String $allowedTypes = this.getAllowedTypes();
        result = result * 59 + ($allowedTypes == null ? 43 : $allowedTypes.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "ContentStorageConfig(id=" + this.getId() + ", storageType=" + this.getStorageType() + ", basePath=" + this.getBasePath() + ", publicUrlPrefix=" + this.getPublicUrlPrefix() + ", accessKey=" + this.getAccessKey() + ", secretKey=" + this.getSecretKey() + ", region=" + this.getRegion() + ", endpoint=" + this.getEndpoint() + ", cdnDomain=" + this.getCdnDomain() + ", maxFileSize=" + this.getMaxFileSize() + ", allowedTypes=" + this.getAllowedTypes() + ", totalQuota=" + this.getTotalQuota() + ", currentUsage=" + this.getCurrentUsage() + ", isActive=" + this.getIsActive() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

