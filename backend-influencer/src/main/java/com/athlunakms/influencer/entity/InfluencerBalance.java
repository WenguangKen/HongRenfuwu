package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="influencer_balance")
public class InfluencerBalance {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false, unique=true)
    private Long influencerId;
    @Column(name="influencer_name", length=100)
    private String influencerName;
    @Column(name="pending_amount", precision=12, scale=2, nullable=false)
    private BigDecimal pendingAmount = BigDecimal.ZERO;
    @Column(name="available_amount", precision=12, scale=2, nullable=false)
    private BigDecimal availableAmount = BigDecimal.ZERO;
    @Column(name="paid_amount", precision=12, scale=2, nullable=false)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(length=10)
    private String currency = "USD";
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addPendingAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.pendingAmount = this.pendingAmount.add(amount);
        }
    }

    public BigDecimal getTotalBalance() {
        return this.pendingAmount.add(this.availableAmount);
    }

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public BigDecimal getPendingAmount() {
        return this.pendingAmount;
    }

    public BigDecimal getAvailableAmount() {
        return this.availableAmount;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public String getCurrency() {
        return this.currency;
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

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
        if (!(o instanceof InfluencerBalance)) {
            return false;
        }
        InfluencerBalance other = (InfluencerBalance)o;
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
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        BigDecimal this$pendingAmount = this.getPendingAmount();
        BigDecimal other$pendingAmount = other.getPendingAmount();
        if (this$pendingAmount == null ? other$pendingAmount != null : !((Object)this$pendingAmount).equals(other$pendingAmount)) {
            return false;
        }
        BigDecimal this$availableAmount = this.getAvailableAmount();
        BigDecimal other$availableAmount = other.getAvailableAmount();
        if (this$availableAmount == null ? other$availableAmount != null : !((Object)this$availableAmount).equals(other$availableAmount)) {
            return false;
        }
        BigDecimal this$paidAmount = this.getPaidAmount();
        BigDecimal other$paidAmount = other.getPaidAmount();
        if (this$paidAmount == null ? other$paidAmount != null : !((Object)this$paidAmount).equals(other$paidAmount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
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
        return other instanceof InfluencerBalance;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        BigDecimal $pendingAmount = this.getPendingAmount();
        result = result * 59 + ($pendingAmount == null ? 43 : ((Object)$pendingAmount).hashCode());
        BigDecimal $availableAmount = this.getAvailableAmount();
        result = result * 59 + ($availableAmount == null ? 43 : ((Object)$availableAmount).hashCode());
        BigDecimal $paidAmount = this.getPaidAmount();
        result = result * 59 + ($paidAmount == null ? 43 : ((Object)$paidAmount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerBalance(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", pendingAmount=" + this.getPendingAmount() + ", availableAmount=" + this.getAvailableAmount() + ", paidAmount=" + this.getPaidAmount() + ", currency=" + this.getCurrency() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }

    public InfluencerBalance() {
    }

    public InfluencerBalance(Long id, Long influencerId, String influencerName, BigDecimal pendingAmount, BigDecimal availableAmount, BigDecimal paidAmount, String currency, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.influencerId = influencerId;
        this.influencerName = influencerName;
        this.pendingAmount = pendingAmount;
        this.availableAmount = availableAmount;
        this.paidAmount = paidAmount;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

