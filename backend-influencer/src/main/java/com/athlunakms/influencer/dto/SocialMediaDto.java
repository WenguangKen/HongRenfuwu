package com.athlunakms.influencer.dto;

public class SocialMediaDto {
    private Long id;
    private String platform;
    private String handle;
    private String url;
    private Long followerCount;
    private Boolean isDefault;

    public Long getId() {
        return this.id;
    }

    public String getPlatform() {
        return this.platform;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getUrl() {
        return this.url;
    }

    public Long getFollowerCount() {
        return this.followerCount;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SocialMediaDto)) {
            return false;
        }
        SocialMediaDto other = (SocialMediaDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$followerCount = this.getFollowerCount();
        Long other$followerCount = other.getFollowerCount();
        if (this$followerCount == null ? other$followerCount != null : !((Object)this$followerCount).equals(other$followerCount)) {
            return false;
        }
        Boolean this$isDefault = this.getIsDefault();
        Boolean other$isDefault = other.getIsDefault();
        if (this$isDefault == null ? other$isDefault != null : !((Object)this$isDefault).equals(other$isDefault)) {
            return false;
        }
        String this$platform = this.getPlatform();
        String other$platform = other.getPlatform();
        if (this$platform == null ? other$platform != null : !this$platform.equals(other$platform)) {
            return false;
        }
        String this$handle = this.getHandle();
        String other$handle = other.getHandle();
        if (this$handle == null ? other$handle != null : !this$handle.equals(other$handle)) {
            return false;
        }
        String this$url = this.getUrl();
        String other$url = other.getUrl();
        return !(this$url == null ? other$url != null : !this$url.equals(other$url));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SocialMediaDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $followerCount = this.getFollowerCount();
        result = result * 59 + ($followerCount == null ? 43 : ((Object)$followerCount).hashCode());
        Boolean $isDefault = this.getIsDefault();
        result = result * 59 + ($isDefault == null ? 43 : ((Object)$isDefault).hashCode());
        String $platform = this.getPlatform();
        result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
        String $handle = this.getHandle();
        result = result * 59 + ($handle == null ? 43 : $handle.hashCode());
        String $url = this.getUrl();
        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        return result;
    }

    public String toString() {
        return "SocialMediaDto(id=" + this.getId() + ", platform=" + this.getPlatform() + ", handle=" + this.getHandle() + ", url=" + this.getUrl() + ", followerCount=" + this.getFollowerCount() + ", isDefault=" + this.getIsDefault() + ")";
    }
}

