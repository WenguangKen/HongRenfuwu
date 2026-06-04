package com.athlunakms.influencer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="influencer_cooperation", indexes={
    @Index(name="idx_coop_influencer", columnList="influencer_id"),
    @Index(name="idx_coop_remittance", columnList="remittance_task_id"),
    @Index(name="idx_coop_status", columnList="status")
})
public class InfluencerCooperation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="order_no")
    private String orderNo;
    @Column(name="cooperation_mode")
    private String cooperationMode;
    @Column(name="type")
    private String type;
    @Column(name="currency")
    private String currency;
    @Column(name="amount")
    private BigDecimal amount;
    @Column(name="status")
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd[ HH:mm:ss]")
    @Column(name="start_date")
    private String startDate;
    @JsonFormat(pattern = "yyyy-MM-dd[ HH:mm:ss]")
    @Column(name="end_date")
    private String endDate;
    @Column(name="payment_method")
    private String paymentMethod;
    @Column(name="payment_type")
    private String paymentType;
    
    @Column(name="remittance_task_id")
    private Long remittanceTaskId;
    
    @Column(name="source_type")
    private String sourceType = "MANUAL";
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="paid_at")
    private LocalDateTime paidAt;
    
    @Column(name="remark", columnDefinition="TEXT")
    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="task_no")
    private String taskNo;

    @Column(name="creator_name")
    private String creatorName;

    @Column(name="auditor_name")
    private String auditorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="audit_time")
    private LocalDateTime auditTime;

    @Column(name="payer_name")
    private String payerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="pay_time")
    private LocalDateTime payTime;

    @Column(name="audit_remark", columnDefinition="TEXT")
    private String auditRemark;

    @Column(name="payment_remark", columnDefinition="TEXT")
    private String paymentRemark;

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

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getCooperationMode() {
        return this.cooperationMode;
    }

    public String getType() {
        return this.type;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getStatus() {
        return this.status;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public LocalDateTime getPaidAt() {
        return this.paidAt;
    }

    public String getRemark() {
        return this.remark;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Long getRemittanceTaskId() {
        return this.remittanceTaskId;
    }

    public String getTaskNo() {
        return this.taskNo;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setCooperationMode(String cooperationMode) {
        this.cooperationMode = cooperationMode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRemittanceTaskId(Long remittanceTaskId) {
        this.remittanceTaskId = remittanceTaskId;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getAuditorName() {
        return this.auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public LocalDateTime getAuditTime() {
        return this.auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getPayerName() {
        return this.payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public LocalDateTime getPayTime() {
        return this.payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public String getAuditRemark() {
        return this.auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getPaymentRemark() {
        return this.paymentRemark;
    }

    public void setPaymentRemark(String paymentRemark) {
        this.paymentRemark = paymentRemark;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerCooperation)) {
            return false;
        }
        InfluencerCooperation other = (InfluencerCooperation)o;
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
        String this$orderNo = this.getOrderNo();
        String other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) {
            return false;
        }
        String this$cooperationMode = this.getCooperationMode();
        String other$cooperationMode = other.getCooperationMode();
        if (this$cooperationMode == null ? other$cooperationMode != null : !this$cooperationMode.equals(other$cooperationMode)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        BigDecimal this$amount = this.getAmount();
        BigDecimal other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !((Object)this$amount).equals(other$amount)) {
            return false;
        }
        String this$paymentMethod = this.getPaymentMethod();
        String other$paymentMethod = other.getPaymentMethod();
        if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod)) {
            return false;
        }
        String this$paymentType = this.getPaymentType();
        String other$paymentType = other.getPaymentType();
        if (this$paymentType == null ? other$paymentType != null : !this$paymentType.equals(other$paymentType)) {
            return false;
        }
        LocalDateTime this$paidAt = this.getPaidAt();
        LocalDateTime other$paidAt = other.getPaidAt();
        if (this$paidAt == null ? other$paidAt != null : !((Object)this$paidAt).equals(other$paidAt)) {
            return false;
        }
        String this$remark = this.getRemark();
        String other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) {
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
        return other instanceof InfluencerCooperation;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        String $cooperationMode = this.getCooperationMode();
        result = result * 59 + ($cooperationMode == null ? 43 : $cooperationMode.hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $paymentMethod = this.getPaymentMethod();
        result = result * 59 + ($paymentMethod == null ? 43 : $paymentMethod.hashCode());
        String $paymentType = this.getPaymentType();
        result = result * 59 + ($paymentType == null ? 43 : $paymentType.hashCode());
        LocalDateTime $paidAt = this.getPaidAt();
        result = result * 59 + ($paidAt == null ? 43 : ((Object)$paidAt).hashCode());
        String $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerCooperation(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", taskNo=" + this.getTaskNo() + ", orderNo=" + this.getOrderNo() + ", cooperationMode=" + this.getCooperationMode() + ", currency=" + this.getCurrency() + ", amount=" + this.getAmount() + ", paymentType=" + this.getPaymentType() + ", paidAt=" + this.getPaidAt() + ", remark=" + this.getRemark() + ", creatorName=" + this.getCreatorName() + ", auditorName=" + this.getAuditorName() + ", auditTime=" + this.getAuditTime() + ", payerName=" + this.getPayerName() + ", payTime=" + this.getPayTime() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

