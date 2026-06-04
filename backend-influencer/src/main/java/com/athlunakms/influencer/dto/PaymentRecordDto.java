package com.athlunakms.influencer.dto;

import com.athlunakms.influencer.entity.InfluencerPayment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRecordDto {
    private Long id;
    private String paymentNo;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private InfluencerPayment.PaymentStatus status;
    private String paymentMethod;
    private String receiverAccount;
    private String batchId;
    private String applicant;
    private String payer;
    private String remark;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
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

    public InfluencerPayment.PaymentStatus getStatus() {
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

    public void setId(Long id) {
        this.id = id;
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

    public void setStatus(InfluencerPayment.PaymentStatus status) {
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PaymentRecordDto)) {
            return false;
        }
        PaymentRecordDto other = (PaymentRecordDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
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
        InfluencerPayment.PaymentStatus this$status = this.getStatus();
        InfluencerPayment.PaymentStatus other$status = other.getStatus();
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
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PaymentRecordDto;
    }

    public int hashCode() {
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $paymentNo = this.getPaymentNo();
        result = result * 59 + ($paymentNo == null ? 43 : $paymentNo.hashCode());
        String $transactionId = this.getTransactionId();
        result = result * 59 + ($transactionId == null ? 43 : $transactionId.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        InfluencerPayment.PaymentStatus $status = this.getStatus();
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
        return result;
    }

    public String toString() {
        return "PaymentRecordDto(id=" + this.getId() + ", paymentNo=" + this.getPaymentNo() + ", transactionId=" + this.getTransactionId() + ", amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", status=" + this.getStatus() + ", paymentMethod=" + this.getPaymentMethod() + ", receiverAccount=" + this.getReceiverAccount() + ", batchId=" + this.getBatchId() + ", applicant=" + this.getApplicant() + ", payer=" + this.getPayer() + ", remark=" + this.getRemark() + ", paidAt=" + this.getPaidAt() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

