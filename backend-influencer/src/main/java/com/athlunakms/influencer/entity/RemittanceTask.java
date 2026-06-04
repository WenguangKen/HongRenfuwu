package com.athlunakms.influencer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "remittance_task", indexes = {
    @Index(name = "idx_remit_influencer", columnList = "influencer_id"),
    @Index(name = "idx_remit_status", columnList = "status")
})
public class RemittanceTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", nullable = false, unique = true, length = 32)
    private String taskNo;

    @Column(name = "influencer_id", nullable = false)
    private Long influencerId;

    @Column(name = "order_no", length = 64)
    private String orderNo;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RemittanceStatus status;

    @Column(name = "payment_type", length = 64)
    private String paymentType;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "voucher_url")
    private String voucherUrl;

    @Column(name = "auditor_id")
    private Long auditorId;

    @Column(name = "auditor_name", length = 64)
    private String auditorName;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "payer_id")
    private Long payerId;

    @Column(name = "payer_name", length = 64)
    private String payerName;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "creator_name", length = 64)
    private String creatorName;

    @Column(name = "audit_remark", columnDefinition = "TEXT")
    private String auditRemark;

    @Column(name = "payment_remark", columnDefinition = "TEXT")
    private String paymentRemark;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "payment_account", length = 200)
    private String paymentAccount;
    
    @Column(name = "payment_details", columnDefinition = "TEXT")
    private String paymentDetails;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RemittanceStatus {
        PENDING_AUDIT,
        PENDING_PAYMENT,
        PAID,
        REJECTED
    }
}
