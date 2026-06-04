package com.athlunakms.influencer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ContentDto {
    private Long id;
    private Long influencerId;
    private String influencerName;
    private String influencerEmail;
    private String title;
    private String description;
    private String status;
    private String platform;
    private String postType;
    private String contentType;
    private String postUrl;
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String thumbnailPath;
    private Integer duration;
    private Integer width;
    private Integer height;
    private String previewUrl;
    private String thumbnailUrl;
    private String taskGroupId;
    private String productSku;
    private String variantTitle;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Integer saves;
    private String orderNo;
    private List<Long> tagIds;
    private String owner;
    private String contactPersonName;
    private String defaultHandle;
    private String ownerName;
    private Long reviewerId;
    private LocalDateTime reviewedAt;
    private String reviewNote;
    @JsonProperty(value="isCommercial")
    private Boolean isCommercial;
    private String remark;
    private Integer contentGroupIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishDate;
    private LocalDateTime socialDataUpdatedAt;
    private BigDecimal engagementRate;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public String getInfluencerEmail() {
        return this.influencerEmail;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus() {
        return this.status;
    }

    public String getPlatform() {
        return this.platform;
    }

    public String getPostType() {
        return this.postType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getPostUrl() {
        return this.postUrl;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public String getFileType() {
        return this.fileType;
    }

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public String getPreviewUrl() {
        return this.previewUrl;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public String getTaskGroupId() {
        return this.taskGroupId;
    }

    public String getProductSku() {
        return this.productSku;
    }

    public String getVariantTitle() {
        return this.variantTitle;
    }

    public Integer getViews() {
        return this.views;
    }

    public Integer getLikes() {
        return this.likes;
    }

    public Integer getComments() {
        return this.comments;
    }

    public Integer getShares() {
        return this.shares;
    }

    public Integer getSaves() {
        return this.saves;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public List<Long> getTagIds() {
        return this.tagIds;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getContactPersonName() {
        return this.contactPersonName;
    }

    public String getDefaultHandle() {
        return this.defaultHandle;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public Long getReviewerId() {
        return this.reviewerId;
    }

    public LocalDateTime getReviewedAt() {
        return this.reviewedAt;
    }

    public String getReviewNote() {
        return this.reviewNote;
    }

    public Boolean getIsCommercial() {
        return this.isCommercial;
    }

    public String getRemark() {
        return this.remark;
    }

    public Integer getContentGroupIndex() {
        return this.contentGroupIndex;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getPublishDate() {
        return this.publishDate;
    }

    public LocalDateTime getSocialDataUpdatedAt() {
        return this.socialDataUpdatedAt;
    }

    public BigDecimal getEngagementRate() {
        return this.engagementRate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setInfluencerEmail(String influencerEmail) {
        this.influencerEmail = influencerEmail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public void setSaves(Integer saves) {
        this.saves = saves;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public void setDefaultHandle(String defaultHandle) {
        this.defaultHandle = defaultHandle;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    @JsonProperty(value="isCommercial")
    public void setIsCommercial(Boolean isCommercial) {
        this.isCommercial = isCommercial;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setContentGroupIndex(Integer contentGroupIndex) {
        this.contentGroupIndex = contentGroupIndex;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public void setSocialDataUpdatedAt(LocalDateTime socialDataUpdatedAt) {
        this.socialDataUpdatedAt = socialDataUpdatedAt;
    }

    public void setEngagementRate(BigDecimal engagementRate) {
        this.engagementRate = engagementRate;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ContentDto)) {
            return false;
        }
        ContentDto other = (ContentDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Long this$fileSize = this.getFileSize();
        Long other$fileSize = other.getFileSize();
        if (this$fileSize == null ? other$fileSize != null : !((Object)this$fileSize).equals(other$fileSize)) {
            return false;
        }
        Integer this$duration = this.getDuration();
        Integer other$duration = other.getDuration();
        if (this$duration == null ? other$duration != null : !((Object)this$duration).equals(other$duration)) {
            return false;
        }
        Integer this$width = this.getWidth();
        Integer other$width = other.getWidth();
        if (this$width == null ? other$width != null : !((Object)this$width).equals(other$width)) {
            return false;
        }
        Integer this$height = this.getHeight();
        Integer other$height = other.getHeight();
        if (this$height == null ? other$height != null : !((Object)this$height).equals(other$height)) {
            return false;
        }
        Integer this$views = this.getViews();
        Integer other$views = other.getViews();
        if (this$views == null ? other$views != null : !((Object)this$views).equals(other$views)) {
            return false;
        }
        Integer this$likes = this.getLikes();
        Integer other$likes = other.getLikes();
        if (this$likes == null ? other$likes != null : !((Object)this$likes).equals(other$likes)) {
            return false;
        }
        Integer this$comments = this.getComments();
        Integer other$comments = other.getComments();
        if (this$comments == null ? other$comments != null : !((Object)this$comments).equals(other$comments)) {
            return false;
        }
        Integer this$shares = this.getShares();
        Integer other$shares = other.getShares();
        if (this$shares == null ? other$shares != null : !((Object)this$shares).equals(other$shares)) {
            return false;
        }
        Long this$reviewerId = this.getReviewerId();
        Long other$reviewerId = other.getReviewerId();
        if (this$reviewerId == null ? other$reviewerId != null : !((Object)this$reviewerId).equals(other$reviewerId)) {
            return false;
        }
        Boolean this$isCommercial = this.getIsCommercial();
        Boolean other$isCommercial = other.getIsCommercial();
        if (this$isCommercial == null ? other$isCommercial != null : !((Object)this$isCommercial).equals(other$isCommercial)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        String this$influencerEmail = this.getInfluencerEmail();
        String other$influencerEmail = other.getInfluencerEmail();
        if (this$influencerEmail == null ? other$influencerEmail != null : !this$influencerEmail.equals(other$influencerEmail)) {
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
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
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
        String this$postUrl = this.getPostUrl();
        String other$postUrl = other.getPostUrl();
        if (this$postUrl == null ? other$postUrl != null : !this$postUrl.equals(other$postUrl)) {
            return false;
        }
        String this$filePath = this.getFilePath();
        String other$filePath = other.getFilePath();
        if (this$filePath == null ? other$filePath != null : !this$filePath.equals(other$filePath)) {
            return false;
        }
        String this$fileName = this.getFileName();
        String other$fileName = other.getFileName();
        if (this$fileName == null ? other$fileName != null : !this$fileName.equals(other$fileName)) {
            return false;
        }
        String this$fileType = this.getFileType();
        String other$fileType = other.getFileType();
        if (this$fileType == null ? other$fileType != null : !this$fileType.equals(other$fileType)) {
            return false;
        }
        String this$thumbnailPath = this.getThumbnailPath();
        String other$thumbnailPath = other.getThumbnailPath();
        if (this$thumbnailPath == null ? other$thumbnailPath != null : !this$thumbnailPath.equals(other$thumbnailPath)) {
            return false;
        }
        String this$previewUrl = this.getPreviewUrl();
        String other$previewUrl = other.getPreviewUrl();
        if (this$previewUrl == null ? other$previewUrl != null : !this$previewUrl.equals(other$previewUrl)) {
            return false;
        }
        String this$thumbnailUrl = this.getThumbnailUrl();
        String other$thumbnailUrl = other.getThumbnailUrl();
        if (this$thumbnailUrl == null ? other$thumbnailUrl != null : !this$thumbnailUrl.equals(other$thumbnailUrl)) {
            return false;
        }
        String this$taskGroupId = this.getTaskGroupId();
        String other$taskGroupId = other.getTaskGroupId();
        if (this$taskGroupId == null ? other$taskGroupId != null : !this$taskGroupId.equals(other$taskGroupId)) {
            return false;
        }
        String this$productSku = this.getProductSku();
        String other$productSku = other.getProductSku();
        if (this$productSku == null ? other$productSku != null : !this$productSku.equals(other$productSku)) {
            return false;
        }
        String this$variantTitle = this.getVariantTitle();
        String other$variantTitle = other.getVariantTitle();
        if (this$variantTitle == null ? other$variantTitle != null : !this$variantTitle.equals(other$variantTitle)) {
            return false;
        }
        String this$orderNo = this.getOrderNo();
        String other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) {
            return false;
        }
        List this$tagIds = this.getTagIds();
        List other$tagIds = other.getTagIds();
        if (this$tagIds == null ? other$tagIds != null : !((Object)this$tagIds).equals(other$tagIds)) {
            return false;
        }
        String this$owner = this.getOwner();
        String other$owner = other.getOwner();
        if (this$owner == null ? other$owner != null : !this$owner.equals(other$owner)) {
            return false;
        }
        String this$contactPersonName = this.getContactPersonName();
        String other$contactPersonName = other.getContactPersonName();
        if (this$contactPersonName == null ? other$contactPersonName != null : !this$contactPersonName.equals(other$contactPersonName)) {
            return false;
        }
        String this$defaultHandle = this.getDefaultHandle();
        String other$defaultHandle = other.getDefaultHandle();
        if (this$defaultHandle == null ? other$defaultHandle != null : !this$defaultHandle.equals(other$defaultHandle)) {
            return false;
        }
        String this$ownerName = this.getOwnerName();
        String other$ownerName = other.getOwnerName();
        if (this$ownerName == null ? other$ownerName != null : !this$ownerName.equals(other$ownerName)) {
            return false;
        }
        LocalDateTime this$reviewedAt = this.getReviewedAt();
        LocalDateTime other$reviewedAt = other.getReviewedAt();
        if (this$reviewedAt == null ? other$reviewedAt != null : !((Object)this$reviewedAt).equals(other$reviewedAt)) {
            return false;
        }
        String this$reviewNote = this.getReviewNote();
        String other$reviewNote = other.getReviewNote();
        if (this$reviewNote == null ? other$reviewNote != null : !this$reviewNote.equals(other$reviewNote)) {
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
        return other instanceof ContentDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $fileSize = this.getFileSize();
        result = result * 59 + ($fileSize == null ? 43 : ((Object)$fileSize).hashCode());
        Integer $duration = this.getDuration();
        result = result * 59 + ($duration == null ? 43 : ((Object)$duration).hashCode());
        Integer $width = this.getWidth();
        result = result * 59 + ($width == null ? 43 : ((Object)$width).hashCode());
        Integer $height = this.getHeight();
        result = result * 59 + ($height == null ? 43 : ((Object)$height).hashCode());
        Integer $views = this.getViews();
        result = result * 59 + ($views == null ? 43 : ((Object)$views).hashCode());
        Integer $likes = this.getLikes();
        result = result * 59 + ($likes == null ? 43 : ((Object)$likes).hashCode());
        Integer $comments = this.getComments();
        result = result * 59 + ($comments == null ? 43 : ((Object)$comments).hashCode());
        Integer $shares = this.getShares();
        result = result * 59 + ($shares == null ? 43 : ((Object)$shares).hashCode());
        Long $reviewerId = this.getReviewerId();
        result = result * 59 + ($reviewerId == null ? 43 : ((Object)$reviewerId).hashCode());
        Boolean $isCommercial = this.getIsCommercial();
        result = result * 59 + ($isCommercial == null ? 43 : ((Object)$isCommercial).hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        String $influencerEmail = this.getInfluencerEmail();
        result = result * 59 + ($influencerEmail == null ? 43 : $influencerEmail.hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $platform = this.getPlatform();
        result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
        String $postType = this.getPostType();
        result = result * 59 + ($postType == null ? 43 : $postType.hashCode());
        String $postUrl = this.getPostUrl();
        result = result * 59 + ($postUrl == null ? 43 : $postUrl.hashCode());
        String $filePath = this.getFilePath();
        result = result * 59 + ($filePath == null ? 43 : $filePath.hashCode());
        String $fileName = this.getFileName();
        result = result * 59 + ($fileName == null ? 43 : $fileName.hashCode());
        String $fileType = this.getFileType();
        result = result * 59 + ($fileType == null ? 43 : $fileType.hashCode());
        String $thumbnailPath = this.getThumbnailPath();
        result = result * 59 + ($thumbnailPath == null ? 43 : $thumbnailPath.hashCode());
        String $previewUrl = this.getPreviewUrl();
        result = result * 59 + ($previewUrl == null ? 43 : $previewUrl.hashCode());
        String $thumbnailUrl = this.getThumbnailUrl();
        result = result * 59 + ($thumbnailUrl == null ? 43 : $thumbnailUrl.hashCode());
        String $taskGroupId = this.getTaskGroupId();
        result = result * 59 + ($taskGroupId == null ? 43 : $taskGroupId.hashCode());
        String $productSku = this.getProductSku();
        result = result * 59 + ($productSku == null ? 43 : $productSku.hashCode());
        String $variantTitle = this.getVariantTitle();
        result = result * 59 + ($variantTitle == null ? 43 : $variantTitle.hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        List $tagIds = this.getTagIds();
        result = result * 59 + ($tagIds == null ? 43 : ((Object)$tagIds).hashCode());
        String $owner = this.getOwner();
        result = result * 59 + ($owner == null ? 43 : $owner.hashCode());
        String $contactPersonName = this.getContactPersonName();
        result = result * 59 + ($contactPersonName == null ? 43 : $contactPersonName.hashCode());
        String $defaultHandle = this.getDefaultHandle();
        result = result * 59 + ($defaultHandle == null ? 43 : $defaultHandle.hashCode());
        String $ownerName = this.getOwnerName();
        result = result * 59 + ($ownerName == null ? 43 : $ownerName.hashCode());
        LocalDateTime $reviewedAt = this.getReviewedAt();
        result = result * 59 + ($reviewedAt == null ? 43 : ((Object)$reviewedAt).hashCode());
        String $reviewNote = this.getReviewNote();
        result = result * 59 + ($reviewNote == null ? 43 : $reviewNote.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "ContentDto(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", influencerEmail=" + this.getInfluencerEmail() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", status=" + this.getStatus() + ", platform=" + this.getPlatform() + ", postType=" + this.getPostType() + ", contentType=" + this.getContentType() + ", postUrl=" + this.getPostUrl() + ", filePath=" + this.getFilePath() + ", fileName=" + this.getFileName() + ", fileSize=" + this.getFileSize() + ", fileType=" + this.getFileType() + ", thumbnailPath=" + this.getThumbnailPath() + ", duration=" + this.getDuration() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ", previewUrl=" + this.getPreviewUrl() + ", thumbnailUrl=" + this.getThumbnailUrl() + ", taskGroupId=" + this.getTaskGroupId() + ", productSku=" + this.getProductSku() + ", variantTitle=" + this.getVariantTitle() + ", views=" + this.getViews() + ", likes=" + this.getLikes() + ", comments=" + this.getComments() + ", shares=" + this.getShares() + ", orderNo=" + this.getOrderNo() + ", tagIds=" + this.getTagIds() + ", owner=" + this.getOwner() + ", contactPersonName=" + this.getContactPersonName() + ", defaultHandle=" + this.getDefaultHandle() + ", ownerName=" + this.getOwnerName() + ", reviewerId=" + this.getReviewerId() + ", reviewedAt=" + this.getReviewedAt() + ", reviewNote=" + this.getReviewNote() + ", isCommercial=" + this.getIsCommercial() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

