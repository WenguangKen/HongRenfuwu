package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="influencer_balance_log")
public class InfluencerBalanceLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="balance_type", nullable=false, length=20)
    private String balanceType;
    @Column(name="change_type", nullable=false, length=20)
    private String changeType;
    @Column(precision=12, scale=2, nullable=false)
    private BigDecimal amount = BigDecimal.ZERO;
    @Column(name="balance_before", precision=12, scale=2, nullable=false)
    private BigDecimal balanceBefore = BigDecimal.ZERO;
    @Column(name="balance_after", precision=12, scale=2, nullable=false)
    private BigDecimal balanceAfter = BigDecimal.ZERO;
    @Column(name="reference_type", length=50)
    private String referenceType;
    @Column(name="reference_id")
    private Long referenceId;
    @Column(length=500)
    private String remark;
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getBalanceType() {
        return this.balanceType;
    }

    public String getChangeType() {
        return this.changeType;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BigDecimal getBalanceBefore() {
        return this.balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return this.balanceAfter;
    }

    public String getReferenceType() {
        return this.referenceType;
    }

    public Long getReferenceId() {
        return this.referenceId;
    }

    public String getRemark() {
        return this.remark;
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

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerBalanceLog)) {
            return false;
        }
        InfluencerBalanceLog other = (InfluencerBalanceLog)o;
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
        Long this$referenceId = this.getReferenceId();
        Long other$referenceId = other.getReferenceId();
        if (this$referenceId == null ? other$referenceId != null : !((Object)this$referenceId).equals(other$referenceId)) {
            return false;
        }
        String this$balanceType = this.getBalanceType();
        String other$balanceType = other.getBalanceType();
        if (this$balanceType == null ? other$balanceType != null : !this$balanceType.equals(other$balanceType)) {
            return false;
        }
        String this$changeType = this.getChangeType();
        String other$changeType = other.getChangeType();
        if (this$changeType == null ? other$changeType != null : !this$changeType.equals(other$changeType)) {
            return false;
        }
        BigDecimal this$amount = this.getAmount();
        BigDecimal other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !((Object)this$amount).equals(other$amount)) {
            return false;
        }
        BigDecimal this$balanceBefore = this.getBalanceBefore();
        BigDecimal other$balanceBefore = other.getBalanceBefore();
        if (this$balanceBefore == null ? other$balanceBefore != null : !((Object)this$balanceBefore).equals(other$balanceBefore)) {
            return false;
        }
        BigDecimal this$balanceAfter = this.getBalanceAfter();
        BigDecimal other$balanceAfter = other.getBalanceAfter();
        if (this$balanceAfter == null ? other$balanceAfter != null : !((Object)this$balanceAfter).equals(other$balanceAfter)) {
            return false;
        }
        String this$referenceType = this.getReferenceType();
        String other$referenceType = other.getReferenceType();
        if (this$referenceType == null ? other$referenceType != null : !this$referenceType.equals(other$referenceType)) {
            return false;
        }
        String this$remark = this.getRemark();
        String other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerBalanceLog;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $referenceId = this.getReferenceId();
        result = result * 59 + ($referenceId == null ? 43 : ((Object)$referenceId).hashCode());
        String $balanceType = this.getBalanceType();
        result = result * 59 + ($balanceType == null ? 43 : $balanceType.hashCode());
        String $changeType = this.getChangeType();
        result = result * 59 + ($changeType == null ? 43 : $changeType.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        BigDecimal $balanceBefore = this.getBalanceBefore();
        result = result * 59 + ($balanceBefore == null ? 43 : ((Object)$balanceBefore).hashCode());
        BigDecimal $balanceAfter = this.getBalanceAfter();
        result = result * 59 + ($balanceAfter == null ? 43 : ((Object)$balanceAfter).hashCode());
        String $referenceType = this.getReferenceType();
        result = result * 59 + ($referenceType == null ? 43 : $referenceType.hashCode());
        String $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerBalanceLog(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", balanceType=" + this.getBalanceType() + ", changeType=" + this.getChangeType() + ", amount=" + this.getAmount() + ", balanceBefore=" + this.getBalanceBefore() + ", balanceAfter=" + this.getBalanceAfter() + ", referenceType=" + this.getReferenceType() + ", referenceId=" + this.getReferenceId() + ", remark=" + this.getRemark() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    public InfluencerBalanceLog() {
    }

    public InfluencerBalanceLog(Long id, Long influencerId, String balanceType, String changeType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, String referenceType, Long referenceId, String remark, LocalDateTime createdAt) {
        this.id = id;
        this.influencerId = influencerId;
        this.balanceType = balanceType;
        this.changeType = changeType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.remark = remark;
        this.createdAt = createdAt;
    }
}

