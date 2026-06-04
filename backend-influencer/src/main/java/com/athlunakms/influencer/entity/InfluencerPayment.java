package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name="influencer_payment", indexes={
    @Index(name="idx_payment_influencer", columnList="influencer_id"),
    @Index(name="idx_payment_status", columnList="status")
})
public class InfluencerPayment {
    public static enum PaymentStatus {
        PENDING,
        APPROVED,
        PAID,
        FAILED,
        CANCELLED;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="payment_no", nullable=false, unique=true)
    private String paymentNo;
    @Column(name="transaction_id")
    private String transactionId;
    @Column(nullable=false)
    private BigDecimal amount;
    private String currency = "USD";
    @Column(nullable=false)
    @Enumerated(value=EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name="payment_method")
    private String paymentMethod;
    @Column(name="receiver_account")
    private String receiverAccount;
    @Column(name="batch_id")
    private String batchId;
    private String applicant;
    private String payer;
    private String remark;
    @Column(name="paid_at")
    private LocalDateTime paidAt;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getPaymentNo() {
        return this.paymentNo;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getReceiverAccount() {
        return this.receiverAccount;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public String getApplicant() {
        return this.applicant;
    }

    public String getPayer() {
        return this.payer;
    }

    public String getRemark() {
        return this.remark;
    }

    public LocalDateTime getPaidAt() {
        return this.paidAt;
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

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
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
        if (!(o instanceof InfluencerPayment)) {
            return false;
        }
        InfluencerPayment other = (InfluencerPayment)o;
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
        String this$paymentNo = this.getPaymentNo();
        String other$paymentNo = other.getPaymentNo();
        if (this$paymentNo == null ? other$paymentNo != null : !this$paymentNo.equals(other$paymentNo)) {
            return false;
        }
        String this$transactionId = this.getTransactionId();
        String other$transactionId = other.getTransactionId();
        if (this$transactionId == null ? other$transactionId != null : !this$transactionId.equals(other$transactionId)) {
            return false;
        }
        BigDecimal this$amount = this.getAmount();
        BigDecimal other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !((Object)this$amount).equals(other$amount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        PaymentStatus this$status = this.getStatus();
        PaymentStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$paymentMethod = this.getPaymentMethod();
        String other$paymentMethod = other.getPaymentMethod();
        if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod)) {
            return false;
        }
        String this$receiverAccount = this.getReceiverAccount();
        String other$receiverAccount = other.getReceiverAccount();
        if (this$receiverAccount == null ? other$receiverAccount != null : !this$receiverAccount.equals(other$receiverAccount)) {
            return false;
        }
        String this$batchId = this.getBatchId();
        String other$batchId = other.getBatchId();
        if (this$batchId == null ? other$batchId != null : !this$batchId.equals(other$batchId)) {
            return false;
        }
        String this$applicant = this.getApplicant();
        String other$applicant = other.getApplicant();
        if (this$applicant == null ? other$applicant != null : !this$applicant.equals(other$applicant)) {
            return false;
        }
        String this$payer = this.getPayer();
        String other$payer = other.getPayer();
        if (this$payer == null ? other$payer != null : !this$payer.equals(other$payer)) {
            return false;
        }
        String this$remark = this.getRemark();
        String other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) {
            return false;
        }
        LocalDateTime this$paidAt = this.getPaidAt();
        LocalDateTime other$paidAt = other.getPaidAt();
        if (this$paidAt == null ? other$paidAt != null : !((Object)this$paidAt).equals(other$paidAt)) {
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
        return other instanceof InfluencerPayment;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $paymentNo = this.getPaymentNo();
        result = result * 59 + ($paymentNo == null ? 43 : $paymentNo.hashCode());
        String $transactionId = this.getTransactionId();
        result = result * 59 + ($transactionId == null ? 43 : $transactionId.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        PaymentStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $paymentMethod = this.getPaymentMethod();
        result = result * 59 + ($paymentMethod == null ? 43 : $paymentMethod.hashCode());
        String $receiverAccount = this.getReceiverAccount();
        result = result * 59 + ($receiverAccount == null ? 43 : $receiverAccount.hashCode());
        String $batchId = this.getBatchId();
        result = result * 59 + ($batchId == null ? 43 : $batchId.hashCode());
        String $applicant = this.getApplicant();
        result = result * 59 + ($applicant == null ? 43 : $applicant.hashCode());
        String $payer = this.getPayer();
        result = result * 59 + ($payer == null ? 43 : $payer.hashCode());
        String $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        LocalDateTime $paidAt = this.getPaidAt();
        result = result * 59 + ($paidAt == null ? 43 : ((Object)$paidAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerPayment(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", paymentNo=" + this.getPaymentNo() + ", transactionId=" + this.getTransactionId() + ", amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", status=" + this.getStatus() + ", paymentMethod=" + this.getPaymentMethod() + ", receiverAccount=" + this.getReceiverAccount() + ", batchId=" + this.getBatchId() + ", applicant=" + this.getApplicant() + ", payer=" + this.getPayer() + ", remark=" + this.getRemark() + ", paidAt=" + this.getPaidAt() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

