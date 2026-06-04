package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="influencer_tag_relation", indexes={
    @Index(name="idx_tagrel_influencer", columnList="influencer_id"),
    @Index(name="idx_tagrel_tag", columnList="tag_id")
})
public class InfluencerTagRelation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="tag_id", nullable=false)
    private Long tagId;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public Long getTagId() {
        return this.tagId;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerTagRelation)) {
            return false;
        }
        InfluencerTagRelation other = (InfluencerTagRelation)o;
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
        Long this$tagId = this.getTagId();
        Long other$tagId = other.getTagId();
        if (this$tagId == null ? other$tagId != null : !((Object)this$tagId).equals(other$tagId)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerTagRelation;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $tagId = this.getTagId();
        result = result * 59 + ($tagId == null ? 43 : ((Object)$tagId).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerTagRelation(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", tagId=" + this.getTagId() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

