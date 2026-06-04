package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "influencer_communication_log")
public class CommunicationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "influencer_id", nullable = false)
    private Long influencerId;

    @Column(columnDefinition = "TEXT")
    private String content;

    /** JSON 数组，存储多张图片 URL，例如 ["/influencer-api/v1/files/xxx","/influencer-api/v1/files/yyy"] */
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;

    @Column(name = "operator_name", length = 100, nullable = false)
    private String operatorName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public CommunicationLog() {}

    public CommunicationLog(Long influencerId, String content, String imageUrls, String operatorName) {
        this.influencerId = influencerId;
        this.content = content;
        this.imageUrls = imageUrls;
        this.operatorName = operatorName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInfluencerId() { return influencerId; }
    public void setInfluencerId(Long influencerId) { this.influencerId = influencerId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
