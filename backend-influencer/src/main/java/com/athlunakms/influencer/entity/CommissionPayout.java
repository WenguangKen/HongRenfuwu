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
@Table(name="commission_payout")
public class CommissionPayout {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="payment_id", length=100, unique=true)
    private String paymentId;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="influencer_name")
    private String influencerName;
    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal amount;
    @Column(nullable=false, length=20)
    private String status = "pending";
    @Column(length=500)
    private String remark;
    @Column(name="approved_by", length=100)
    private String approvedBy;
    @Column(name="approved_at")
    private LocalDateTime approvedAt;
    @Column(name="paid_at")
    private LocalDateTime paidAt;
    @Column(name="payment_method", length=50)
    private String paymentMethod;
    @Column(name="payment_account", length=200)
    private String paymentAccount;
    @Column(name="payment_reference", length=100)
    private String paymentReference;
    @Column(name="created_by", length=100)
    private String createdBy;
    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;
    @Column(name="payment_details", columnDefinition="TEXT")
    private String paymentDetails;
    @Column(name="rejected_at")
    private LocalDateTime rejectedAt;
    @Column(name="rejected_by", length=100)
    private String rejectedBy;
    @Column(name="review_remark", length=500)
    private String reviewRemark;
    @Column(name="fee", precision=12, scale=2)
    private BigDecimal fee;
    @Column(name="total_amount", precision=12, scale=2)
    private BigDecimal totalAmount;
    @Column(name="actual_paid_at")
    private LocalDateTime actualPaidAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getStatus() {
        return this.status;
    }

    public String getRemark() {
        return this.remark;
    }

    public String getApprovedBy() {
        return this.approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return this.approvedAt;
    }

    public LocalDateTime getPaidAt() {
        return this.paidAt;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getPaymentAccount() {
        return this.paymentAccount;
    }

    public String getPaymentReference() {
        return this.paymentReference;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public String getPaymentDetails() {
        return this.paymentDetails;
    }

    public LocalDateTime getRejectedAt() {
        return this.rejectedAt;
    }

    public String getRejectedBy() {
        return this.rejectedBy;
    }

    public String getReviewRemark() {
        return this.reviewRemark;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public BigDecimal getFee() {
        return this.fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getActualPaidAt() {
        return this.actualPaidAt;
    }

    public void setActualPaidAt(LocalDateTime actualPaidAt) {
        this.actualPaidAt = actualPaidAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CommissionPayout)) {
            return false;
        }
        CommissionPayout other = (CommissionPayout)o;
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
        String this$paymentId = this.getPaymentId();
        String other$paymentId = other.getPaymentId();
        if (this$paymentId == null ? other$paymentId != null : !this$paymentId.equals(other$paymentId)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        BigDecimal this$amount = this.getAmount();
        BigDecimal other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !((Object)this$amount).equals(other$amount)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$remark = this.getRemark();
        String other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) {
            return false;
        }
        String this$approvedBy = this.getApprovedBy();
        String other$approvedBy = other.getApprovedBy();
        if (this$approvedBy == null ? other$approvedBy != null : !this$approvedBy.equals(other$approvedBy)) {
            return false;
        }
        LocalDateTime this$approvedAt = this.getApprovedAt();
        LocalDateTime other$approvedAt = other.getApprovedAt();
        if (this$approvedAt == null ? other$approvedAt != null : !((Object)this$approvedAt).equals(other$approvedAt)) {
            return false;
        }
        LocalDateTime this$paidAt = this.getPaidAt();
        LocalDateTime other$paidAt = other.getPaidAt();
        if (this$paidAt == null ? other$paidAt != null : !((Object)this$paidAt).equals(other$paidAt)) {
            return false;
        }
        String this$paymentMethod = this.getPaymentMethod();
        String other$paymentMethod = other.getPaymentMethod();
        if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod)) {
            return false;
        }
        String this$paymentAccount = this.getPaymentAccount();
        String other$paymentAccount = other.getPaymentAccount();
        if (this$paymentAccount == null ? other$paymentAccount != null : !this$paymentAccount.equals(other$paymentAccount)) {
            return false;
        }
        String this$paymentReference = this.getPaymentReference();
        String other$paymentReference = other.getPaymentReference();
        if (this$paymentReference == null ? other$paymentReference != null : !this$paymentReference.equals(other$paymentReference)) {
            return false;
        }
        String this$createdBy = this.getCreatedBy();
        String other$createdBy = other.getCreatedBy();
        if (this$createdBy == null ? other$createdBy != null : !this$createdBy.equals(other$createdBy)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
            return false;
        }
        String this$paymentDetails = this.getPaymentDetails();
        String other$paymentDetails = other.getPaymentDetails();
        if (this$paymentDetails == null ? other$paymentDetails != null : !this$paymentDetails.equals(other$paymentDetails)) {
            return false;
        }
        LocalDateTime this$rejectedAt = this.getRejectedAt();
        LocalDateTime other$rejectedAt = other.getRejectedAt();
        if (this$rejectedAt == null ? other$rejectedAt != null : !((Object)this$rejectedAt).equals(other$rejectedAt)) {
            return false;
        }
        String this$rejectedBy = this.getRejectedBy();
        String other$rejectedBy = other.getRejectedBy();
        if (this$rejectedBy == null ? other$rejectedBy != null : !this$rejectedBy.equals(other$rejectedBy)) {
            return false;
        }
        String this$reviewRemark = this.getReviewRemark();
        String other$reviewRemark = other.getReviewRemark();
        return !(this$reviewRemark == null ? other$reviewRemark != null : !this$reviewRemark.equals(other$reviewRemark));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CommissionPayout;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $paymentId = this.getPaymentId();
        result = result * 59 + ($paymentId == null ? 43 : $paymentId.hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        String $approvedBy = this.getApprovedBy();
        result = result * 59 + ($approvedBy == null ? 43 : $approvedBy.hashCode());
        LocalDateTime $approvedAt = this.getApprovedAt();
        result = result * 59 + ($approvedAt == null ? 43 : ((Object)$approvedAt).hashCode());
        LocalDateTime $paidAt = this.getPaidAt();
        result = result * 59 + ($paidAt == null ? 43 : ((Object)$paidAt).hashCode());
        String $paymentMethod = this.getPaymentMethod();
        result = result * 59 + ($paymentMethod == null ? 43 : $paymentMethod.hashCode());
        String $paymentAccount = this.getPaymentAccount();
        result = result * 59 + ($paymentAccount == null ? 43 : $paymentAccount.hashCode());
        String $paymentReference = this.getPaymentReference();
        result = result * 59 + ($paymentReference == null ? 43 : $paymentReference.hashCode());
        String $createdBy = this.getCreatedBy();
        result = result * 59 + ($createdBy == null ? 43 : $createdBy.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        String $paymentDetails = this.getPaymentDetails();
        result = result * 59 + ($paymentDetails == null ? 43 : $paymentDetails.hashCode());
        LocalDateTime $rejectedAt = this.getRejectedAt();
        result = result * 59 + ($rejectedAt == null ? 43 : ((Object)$rejectedAt).hashCode());
        String $rejectedBy = this.getRejectedBy();
        result = result * 59 + ($rejectedBy == null ? 43 : $rejectedBy.hashCode());
        String $reviewRemark = this.getReviewRemark();
        result = result * 59 + ($reviewRemark == null ? 43 : $reviewRemark.hashCode());
        return result;
    }

    public String toString() {
        return "CommissionPayout(id=" + this.getId() + ", paymentId=" + this.getPaymentId() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", amount=" + this.getAmount() + ", status=" + this.getStatus() + ", remark=" + this.getRemark() + ", approvedBy=" + this.getApprovedBy() + ", approvedAt=" + this.getApprovedAt() + ", paidAt=" + this.getPaidAt() + ", paymentMethod=" + this.getPaymentMethod() + ", paymentAccount=" + this.getPaymentAccount() + ", paymentReference=" + this.getPaymentReference() + ", createdBy=" + this.getCreatedBy() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", paymentDetails=" + this.getPaymentDetails() + ", rejectedAt=" + this.getRejectedAt() + ", rejectedBy=" + this.getRejectedBy() + ", reviewRemark=" + this.getReviewRemark() + ")";
    }
}

