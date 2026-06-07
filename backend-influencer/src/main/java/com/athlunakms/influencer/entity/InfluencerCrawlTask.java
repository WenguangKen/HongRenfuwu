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
@Table(name = "influencer_crawl_task")
public class InfluencerCrawlTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "strategy_type", nullable = false)
    private String strategyType; // HASHTAG / COMPETITOR_FOLLOWER / KEYWORD

    @Column(name = "search_query", nullable = false)
    private String searchQuery;

    @Column(name = "min_followers")
    private Long minFollowers = 0L;

    @Column(name = "max_followers")
    private Long maxFollowers = 10000000L;

    @Column(name = "target_brand")
    private String targetBrand;

    @Column(name = "auto_brand_mentions")
    private Boolean autoBrandMentions = true;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING / RUNNING / COMPLETED / FAILED

    @Column(name = "scraped_count")
    private Integer scrapedCount = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Long getMinFollowers() {
        return minFollowers;
    }

    public void setMinFollowers(Long minFollowers) {
        this.minFollowers = minFollowers;
    }

    public Long getMaxFollowers() {
        return maxFollowers;
    }

    public void setMaxFollowers(Long maxFollowers) {
        this.maxFollowers = maxFollowers;
    }

    public String getTargetBrand() {
        return targetBrand;
    }

    public void setTargetBrand(String targetBrand) {
        this.targetBrand = targetBrand;
    }

    public Boolean getAutoBrandMentions() {
        return autoBrandMentions;
    }

    public void setAutoBrandMentions(Boolean autoBrandMentions) {
        this.autoBrandMentions = autoBrandMentions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getScrapedCount() {
        return scrapedCount;
    }

    public void setScrapedCount(Integer scrapedCount) {
        this.scrapedCount = scrapedCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
