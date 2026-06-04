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
@Table(name="import_jobs")
public class ImportJob {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String status;
    private Integer totalCount;
    private Integer processedCount;
    private Integer successCount;
    private Integer failCount;
    @Column(columnDefinition="TEXT")
    private String errorMessage;
    @Column(columnDefinition="TEXT")
    private String resultJson;
    @CreationTimestamp
    @Column(updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getStatus() {
        return this.status;
    }

    public Integer getTotalCount() {
        return this.totalCount;
    }

    public Integer getProcessedCount() {
        return this.processedCount;
    }

    public Integer getSuccessCount() {
        return this.successCount;
    }

    public Integer getFailCount() {
        return this.failCount;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getResultJson() {
        return this.resultJson;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setProcessedCount(Integer processedCount) {
        this.processedCount = processedCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
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
        if (!(o instanceof ImportJob)) {
            return false;
        }
        ImportJob other = (ImportJob)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$totalCount = this.getTotalCount();
        Integer other$totalCount = other.getTotalCount();
        if (this$totalCount == null ? other$totalCount != null : !((Object)this$totalCount).equals(other$totalCount)) {
            return false;
        }
        Integer this$processedCount = this.getProcessedCount();
        Integer other$processedCount = other.getProcessedCount();
        if (this$processedCount == null ? other$processedCount != null : !((Object)this$processedCount).equals(other$processedCount)) {
            return false;
        }
        Integer this$successCount = this.getSuccessCount();
        Integer other$successCount = other.getSuccessCount();
        if (this$successCount == null ? other$successCount != null : !((Object)this$successCount).equals(other$successCount)) {
            return false;
        }
        Integer this$failCount = this.getFailCount();
        Integer other$failCount = other.getFailCount();
        if (this$failCount == null ? other$failCount != null : !((Object)this$failCount).equals(other$failCount)) {
            return false;
        }
        String this$type = this.getType();
        String other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$errorMessage = this.getErrorMessage();
        String other$errorMessage = other.getErrorMessage();
        if (this$errorMessage == null ? other$errorMessage != null : !this$errorMessage.equals(other$errorMessage)) {
            return false;
        }
        String this$resultJson = this.getResultJson();
        String other$resultJson = other.getResultJson();
        if (this$resultJson == null ? other$resultJson != null : !this$resultJson.equals(other$resultJson)) {
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
        return other instanceof ImportJob;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $totalCount = this.getTotalCount();
        result = result * 59 + ($totalCount == null ? 43 : ((Object)$totalCount).hashCode());
        Integer $processedCount = this.getProcessedCount();
        result = result * 59 + ($processedCount == null ? 43 : ((Object)$processedCount).hashCode());
        Integer $successCount = this.getSuccessCount();
        result = result * 59 + ($successCount == null ? 43 : ((Object)$successCount).hashCode());
        Integer $failCount = this.getFailCount();
        result = result * 59 + ($failCount == null ? 43 : ((Object)$failCount).hashCode());
        String $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $errorMessage = this.getErrorMessage();
        result = result * 59 + ($errorMessage == null ? 43 : $errorMessage.hashCode());
        String $resultJson = this.getResultJson();
        result = result * 59 + ($resultJson == null ? 43 : $resultJson.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "ImportJob(id=" + this.getId() + ", type=" + this.getType() + ", status=" + this.getStatus() + ", totalCount=" + this.getTotalCount() + ", processedCount=" + this.getProcessedCount() + ", successCount=" + this.getSuccessCount() + ", failCount=" + this.getFailCount() + ", errorMessage=" + this.getErrorMessage() + ", resultJson=" + this.getResultJson() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

