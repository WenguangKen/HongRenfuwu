package com.athlunakms.influencer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="influencer_content", indexes={
    @Index(name="idx_content_influencer", columnList="influencer_id"),
    @Index(name="idx_content_status", columnList="status"),
    @Index(name="idx_content_taskgroup", columnList="task_group_id"),
    @Index(name="idx_content_taskgroup_cgi", columnList="task_group_id, content_group_index"),
    @Index(name="idx_content_created", columnList="created_at DESC"),
    @Index(name="idx_content_orderno", columnList="order_no")
})
public class InfluencerContent {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="owner")
    private String owner;
    @Column(name="order_no")
    private String orderNo;
    @Column(name="product_sku", columnDefinition="TEXT")
    private String productSku;
    @Column(name="variant_title", columnDefinition="TEXT")
    private String variantTitle;
    @Column(name="task_group_id")
    private String taskGroupId;
    @Column(name="tags", columnDefinition="json")
    private String tags;
    @Column(name="title")
    private String title;
    @Column(name="description", columnDefinition="TEXT")
    private String description;
    @Column(name="status")
    private String status = "DRAFT";
    @Column(name="platform")
    private String platform;
    @Column(name="post_url")
    private String postUrl;
    @Column(name="post_type")
    private String postType = "POST";
    @Column(name="media_url")
    private String mediaUrl;
    @Column(name="file_path")
    private String filePath;
    @Column(name="file_name")
    private String fileName;
    @Column(name="file_size")
    private Long fileSize;
    @Column(name="file_type")
    private String fileType;
    @Column(name="thumbnail_path")
    private String thumbnailPath;
    @Column(name="duration")
    private Integer duration;
    @Column(name="width")
    private Integer width;
    @Column(name="height")
    private Integer height;
    @JsonFormat(pattern = "yyyy-MM-dd[ HH:mm:ss]")
    @Column(name="publish_date")
    private LocalDateTime publishDate;
    @Column(name="content_type")
    private String contentType;
    @Column(name="engagement_rate")
    private BigDecimal engagementRate;
    @Column(name="views")
    private Integer views = 0;
    @Column(name="likes")
    private Integer likes = 0;
    @Column(name="comments")
    private Integer comments = 0;
    @Column(name="shares")
    private Integer shares = 0;
    @Column(name="saves")
    private Integer saves = 0;
    @Column(name="reviewer_id")
    private Long reviewerId;
    @Column(name="reviewed_at")
    private LocalDateTime reviewedAt;
    @Column(name="review_note")
    private String reviewNote;
    @Column(name="is_commercial")
    private Boolean isCommercial = true;
    @Column(name="remark", columnDefinition="TEXT")
    private String remark;
    @Column(name="content_group_index", nullable=false)
    private Integer contentGroupIndex = 0;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name="social_data_updated_at")
    private LocalDateTime socialDataUpdatedAt;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
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

    public String getVariantTitle() {
        return this.variantTitle;
    }

    public String getTaskGroupId() {
        return this.taskGroupId;
    }

    public String getTags() {
        return this.tags;
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

    public String getPostUrl() {
        return this.postUrl;
    }

    public String getPostType() {
        return this.postType;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
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

    public LocalDateTime getPublishDate() {
        return this.publishDate;
    }

    public String getContentType() {
        return this.contentType;
    }

    public BigDecimal getEngagementRate() {
        return this.engagementRate;
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

    public LocalDateTime getSocialDataUpdatedAt() {
        return this.socialDataUpdatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
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

    public void setVariantTitle(String variantTitle) {
        this.variantTitle = variantTitle;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
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

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setEngagementRate(BigDecimal engagementRate) {
        this.engagementRate = engagementRate;
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

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

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

    public void setSocialDataUpdatedAt(LocalDateTime socialDataUpdatedAt) {
        this.socialDataUpdatedAt = socialDataUpdatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerContent)) {
            return false;
        }
        InfluencerContent other = (InfluencerContent)o;
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
        String this$variantTitle = this.getVariantTitle();
        String other$variantTitle = other.getVariantTitle();
        if (this$variantTitle == null ? other$variantTitle != null : !this$variantTitle.equals(other$variantTitle)) {
            return false;
        }
        String this$taskGroupId = this.getTaskGroupId();
        String other$taskGroupId = other.getTaskGroupId();
        if (this$taskGroupId == null ? other$taskGroupId != null : !this$taskGroupId.equals(other$taskGroupId)) {
            return false;
        }
        String this$tags = this.getTags();
        String other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) {
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
        String this$postUrl = this.getPostUrl();
        String other$postUrl = other.getPostUrl();
        if (this$postUrl == null ? other$postUrl != null : !this$postUrl.equals(other$postUrl)) {
            return false;
        }
        String this$postType = this.getPostType();
        String other$postType = other.getPostType();
        if (this$postType == null ? other$postType != null : !this$postType.equals(other$postType)) {
            return false;
        }
        String this$mediaUrl = this.getMediaUrl();
        String other$mediaUrl = other.getMediaUrl();
        if (this$mediaUrl == null ? other$mediaUrl != null : !this$mediaUrl.equals(other$mediaUrl)) {
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
        LocalDateTime this$publishDate = this.getPublishDate();
        LocalDateTime other$publishDate = other.getPublishDate();
        if (this$publishDate == null ? other$publishDate != null : !((Object)this$publishDate).equals(other$publishDate)) {
            return false;
        }
        BigDecimal this$engagementRate = this.getEngagementRate();
        BigDecimal other$engagementRate = other.getEngagementRate();
        if (this$engagementRate == null ? other$engagementRate != null : !((Object)this$engagementRate).equals(other$engagementRate)) {
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
        return other instanceof InfluencerContent;
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
        String $owner = this.getOwner();
        result = result * 59 + ($owner == null ? 43 : $owner.hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        String $productSku = this.getProductSku();
        result = result * 59 + ($productSku == null ? 43 : $productSku.hashCode());
        String $variantTitle = this.getVariantTitle();
        result = result * 59 + ($variantTitle == null ? 43 : $variantTitle.hashCode());
        String $taskGroupId = this.getTaskGroupId();
        result = result * 59 + ($taskGroupId == null ? 43 : $taskGroupId.hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $platform = this.getPlatform();
        result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
        String $postUrl = this.getPostUrl();
        result = result * 59 + ($postUrl == null ? 43 : $postUrl.hashCode());
        String $postType = this.getPostType();
        result = result * 59 + ($postType == null ? 43 : $postType.hashCode());
        String $mediaUrl = this.getMediaUrl();
        result = result * 59 + ($mediaUrl == null ? 43 : $mediaUrl.hashCode());
        String $filePath = this.getFilePath();
        result = result * 59 + ($filePath == null ? 43 : $filePath.hashCode());
        String $fileName = this.getFileName();
        result = result * 59 + ($fileName == null ? 43 : $fileName.hashCode());
        String $fileType = this.getFileType();
        result = result * 59 + ($fileType == null ? 43 : $fileType.hashCode());
        String $thumbnailPath = this.getThumbnailPath();
        result = result * 59 + ($thumbnailPath == null ? 43 : $thumbnailPath.hashCode());
        LocalDateTime $publishDate = this.getPublishDate();
        result = result * 59 + ($publishDate == null ? 43 : ((Object)$publishDate).hashCode());
        BigDecimal $engagementRate = this.getEngagementRate();
        result = result * 59 + ($engagementRate == null ? 43 : ((Object)$engagementRate).hashCode());
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
        return "InfluencerContent(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", owner=" + this.getOwner() + ", orderNo=" + this.getOrderNo() + ", productSku=" + this.getProductSku() + ", variantTitle=" + this.getVariantTitle() + ", taskGroupId=" + this.getTaskGroupId() + ", tags=" + this.getTags() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", status=" + this.getStatus() + ", platform=" + this.getPlatform() + ", contentType=" + this.getContentType() + ", postUrl=" + this.getPostUrl() + ", postType=" + this.getPostType() + ", mediaUrl=" + this.getMediaUrl() + ", filePath=" + this.getFilePath() + ", fileName=" + this.getFileName() + ", fileSize=" + this.getFileSize() + ", fileType=" + this.getFileType() + ", thumbnailPath=" + this.getThumbnailPath() + ", duration=" + this.getDuration() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ", publishDate=" + this.getPublishDate() + ", engagementRate=" + this.getEngagementRate() + ", views=" + this.getViews() + ", likes=" + this.getLikes() + ", comments=" + this.getComments() + ", shares=" + this.getShares() + ", reviewerId=" + this.getReviewerId() + ", reviewedAt=" + this.getReviewedAt() + ", reviewNote=" + this.getReviewNote() + ", isCommercial=" + this.getIsCommercial() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

