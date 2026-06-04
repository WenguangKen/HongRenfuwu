package com.athlunakms.influencer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ContentCreateDto {
    private Long influencerId;
    private String title;
    private String description;
    private String platform;
    private String postType;
    private String owner;
    private String orderNo;
    private String productSku;
    private String postUrl;
    private String contentType;
    private String taskGroupId;
    private String status;
    private List<Long> tagIds;
    @JsonProperty(value="isCommercial")
    private Boolean isCommercial;
    private String remark;
    private java.time.LocalDateTime publishDate;
    private Integer contentGroupIndex;

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPlatform() {
        return this.platform;
    }

    public String getPostType() {
        return this.postType;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getProductSku() {
        return this.productSku;
    }

    public String getPostUrl() {
        return this.postUrl;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getTaskGroupId() {
        return this.taskGroupId;
    }

    public String getStatus() {
        return this.status;
    }

    public List<Long> getTagIds() {
        return this.tagIds;
    }

    public Boolean getIsCommercial() {
        return this.isCommercial;
    }

    public String getRemark() {
        return this.remark;
    }

    public java.time.LocalDateTime getPublishDate() {
        return this.publishDate;
    }

    public Integer getContentGroupIndex() {
        return this.contentGroupIndex;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    @JsonProperty(value="isCommercial")
    public void setIsCommercial(Boolean isCommercial) {
        this.isCommercial = isCommercial;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setPublishDate(java.time.LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public void setContentGroupIndex(Integer contentGroupIndex) {
        this.contentGroupIndex = contentGroupIndex;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ContentCreateDto)) {
            return false;
        }
        ContentCreateDto other = (ContentCreateDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Boolean this$isCommercial = this.getIsCommercial();
        Boolean other$isCommercial = other.getIsCommercial();
        if (this$isCommercial == null ? other$isCommercial != null : !((Object)this$isCommercial).equals(other$isCommercial)) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$platform = this.getPlatform();
        String other$platform = other.getPlatform();
        if (this$platform == null ? other$platform != null : !this$platform.equals(other$platform)) {
            return false;
        }
        String this$postType = this.getPostType();
        String other$postType = other.getPostType();
        if (this$postType == null ? other$postType != null : !this$postType.equals(other$postType)) {
            return false;
        }
        String this$owner = this.getOwner();
        String other$owner = other.getOwner();
        if (this$owner == null ? other$owner != null : !this$owner.equals(other$owner)) {
            return false;
        }
        String this$orderNo = this.getOrderNo();
        String other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) {
            return false;
        }
        String this$productSku = this.getProductSku();
        String other$productSku = other.getProductSku();
        if (this$productSku == null ? other$productSku != null : !this$productSku.equals(other$productSku)) {
            return false;
        }
        String this$postUrl = this.getPostUrl();
        String other$postUrl = other.getPostUrl();
        if (this$postUrl == null ? other$postUrl != null : !this$postUrl.equals(other$postUrl)) {
            return false;
        }
        String this$taskGroupId = this.getTaskGroupId();
        String other$taskGroupId = other.getTaskGroupId();
        if (this$taskGroupId == null ? other$taskGroupId != null : !this$taskGroupId.equals(other$taskGroupId)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        List this$tagIds = this.getTagIds();
        List other$tagIds = other.getTagIds();
        return !(this$tagIds == null ? other$tagIds != null : !((Object)this$tagIds).equals(other$tagIds));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ContentCreateDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Boolean $isCommercial = this.getIsCommercial();
        result = result * 59 + ($isCommercial == null ? 43 : ((Object)$isCommercial).hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $platform = this.getPlatform();
        result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
        String $postType = this.getPostType();
        result = result * 59 + ($postType == null ? 43 : $postType.hashCode());
        String $owner = this.getOwner();
        result = result * 59 + ($owner == null ? 43 : $owner.hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        String $productSku = this.getProductSku();
        result = result * 59 + ($productSku == null ? 43 : $productSku.hashCode());
        String $postUrl = this.getPostUrl();
        result = result * 59 + ($postUrl == null ? 43 : $postUrl.hashCode());
        String $taskGroupId = this.getTaskGroupId();
        result = result * 59 + ($taskGroupId == null ? 43 : $taskGroupId.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        List $tagIds = this.getTagIds();
        result = result * 59 + ($tagIds == null ? 43 : ((Object)$tagIds).hashCode());
        return result;
    }

    public String toString() {
        return "ContentCreateDto(influencerId=" + this.getInfluencerId() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", platform=" + this.getPlatform() + ", postType=" + this.getPostType() + ", owner=" + this.getOwner() + ", orderNo=" + this.getOrderNo() + ", productSku=" + this.getProductSku() + ", postUrl=" + this.getPostUrl() + ", contentType=" + this.getContentType() + ", taskGroupId=" + this.getTaskGroupId() + ", status=" + this.getStatus() + ", tagIds=" + this.getTagIds() + ", isCommercial=" + this.getIsCommercial() + ")";
    }
}

