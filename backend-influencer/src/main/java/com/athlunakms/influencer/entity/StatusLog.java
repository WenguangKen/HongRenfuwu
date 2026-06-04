package com.athlunakms.influencer.entity;

import com.athlunakms.influencer.entity.Influencer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="influencer_status_log", indexes={
    @Index(name="idx_statuslog_influencer", columnList="influencer_id")
})
public class StatusLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="from_status", length=50)
    @Enumerated(value=EnumType.STRING)
    private Influencer.Status fromStatus;
    @Column(name="to_status", length=50)
    @Enumerated(value=EnumType.STRING)
    private Influencer.Status toStatus;
    @Column(name="from_stage", length=50)
    @Enumerated(value=EnumType.STRING)
    private Influencer.Stage fromStage;
    @Column(name="to_stage", length=50)
    @Enumerated(value=EnumType.STRING)
    private Influencer.Stage toStage;
    @Column(nullable=false)
    private String reason;
    @Column(name="operator_id")
    private Long operatorId;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public Influencer.Status getFromStatus() {
        return this.fromStatus;
    }

    public Influencer.Status getToStatus() {
        return this.toStatus;
    }

    public Influencer.Stage getFromStage() {
        return this.fromStage;
    }

    public Influencer.Stage getToStage() {
        return this.toStage;
    }

    public String getReason() {
        return this.reason;
    }

    public Long getOperatorId() {
        return this.operatorId;
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

    public void setFromStatus(Influencer.Status fromStatus) {
        this.fromStatus = fromStatus;
    }

    public void setToStatus(Influencer.Status toStatus) {
        this.toStatus = toStatus;
    }

    public void setFromStage(Influencer.Stage fromStage) {
        this.fromStage = fromStage;
    }

    public void setToStage(Influencer.Stage toStage) {
        this.toStage = toStage;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatusLog)) {
            return false;
        }
        StatusLog other = (StatusLog)o;
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
        Long this$operatorId = this.getOperatorId();
        Long other$operatorId = other.getOperatorId();
        if (this$operatorId == null ? other$operatorId != null : !((Object)this$operatorId).equals(other$operatorId)) {
            return false;
        }
        Influencer.Status this$fromStatus = this.getFromStatus();
        Influencer.Status other$fromStatus = other.getFromStatus();
        if (this$fromStatus == null ? other$fromStatus != null : !this$fromStatus.equals(other$fromStatus)) {
            return false;
        }
        Influencer.Status this$toStatus = this.getToStatus();
        Influencer.Status other$toStatus = other.getToStatus();
        if (this$toStatus == null ? other$toStatus != null : !this$toStatus.equals(other$toStatus)) {
            return false;
        }
        Influencer.Stage this$fromStage = this.getFromStage();
        Influencer.Stage other$fromStage = other.getFromStage();
        if (this$fromStage == null ? other$fromStage != null : !this$fromStage.equals(other$fromStage)) {
            return false;
        }
        Influencer.Stage this$toStage = this.getToStage();
        Influencer.Stage other$toStage = other.getToStage();
        if (this$toStage == null ? other$toStage != null : !this$toStage.equals(other$toStage)) {
            return false;
        }
        String this$reason = this.getReason();
        String other$reason = other.getReason();
        if (this$reason == null ? other$reason != null : !this$reason.equals(other$reason)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof StatusLog;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $operatorId = this.getOperatorId();
        result = result * 59 + ($operatorId == null ? 43 : ((Object)$operatorId).hashCode());
        Influencer.Status $fromStatus = this.getFromStatus();
        result = result * 59 + ($fromStatus == null ? 43 : $fromStatus.hashCode());
        Influencer.Status $toStatus = this.getToStatus();
        result = result * 59 + ($toStatus == null ? 43 : $toStatus.hashCode());
        Influencer.Stage $fromStage = this.getFromStage();
        result = result * 59 + ($fromStage == null ? 43 : $fromStage.hashCode());
        Influencer.Stage $toStage = this.getToStage();
        result = result * 59 + ($toStage == null ? 43 : $toStage.hashCode());
        String $reason = this.getReason();
        result = result * 59 + ($reason == null ? 43 : $reason.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "StatusLog(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", fromStatus=" + this.getFromStatus() + ", toStatus=" + this.getToStatus() + ", fromStage=" + this.getFromStage() + ", toStage=" + this.getToStage() + ", reason=" + this.getReason() + ", operatorId=" + this.getOperatorId() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

