package com.athlunakms.influencer.dto;

public class StorageConfigDto {
    private Long id;
    private String storageType;
    private String basePath;
    private String publicUrlPrefix;
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String cdnDomain;
    private Long maxFileSize;
    private String allowedTypes;
    private Boolean isActive;

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

    public Boolean getIsActive() {
        return this.isActive;
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

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StorageConfigDto)) {
            return false;
        }
        StorageConfigDto other = (StorageConfigDto)o;
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
        return !(this$allowedTypes == null ? other$allowedTypes != null : !this$allowedTypes.equals(other$allowedTypes));
    }

    protected boolean canEqual(Object other) {
        return other instanceof StorageConfigDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $maxFileSize = this.getMaxFileSize();
        result = result * 59 + ($maxFileSize == null ? 43 : ((Object)$maxFileSize).hashCode());
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
        return result;
    }

    public String toString() {
        return "StorageConfigDto(id=" + this.getId() + ", storageType=" + this.getStorageType() + ", basePath=" + this.getBasePath() + ", publicUrlPrefix=" + this.getPublicUrlPrefix() + ", accessKey=" + this.getAccessKey() + ", secretKey=" + this.getSecretKey() + ", region=" + this.getRegion() + ", endpoint=" + this.getEndpoint() + ", cdnDomain=" + this.getCdnDomain() + ", maxFileSize=" + this.getMaxFileSize() + ", allowedTypes=" + this.getAllowedTypes() + ", isActive=" + this.getIsActive() + ")";
    }
}

