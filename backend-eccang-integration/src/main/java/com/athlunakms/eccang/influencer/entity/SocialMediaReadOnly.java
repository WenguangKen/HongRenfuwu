package com.athlunakms.eccang.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="influencer_social_media")
public class SocialMediaReadOnly {
    @Id
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(nullable=false)
    private String platform;
    @Column(nullable=false)
    private String handle;
    private String url;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
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
}
