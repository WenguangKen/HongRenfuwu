package com.athlunakms.influencer.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RemittanceTaskDto {
    private Long id;
    private String taskNo;
    private Long influencerId;
    private String influencerName;
    private String influencerEmail;
    private String orderNo;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentType;
    private String remark;
    private String voucherUrl;
    private Long auditorId;
    private String auditorName;
    private LocalDateTime auditTime;
    private Long payerId;
    private String payerName;
    private LocalDateTime payTime;
    private String auditRemark;
    private String paymentRemark;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentMethod;
    private String paymentAccount;
    private String paymentDetails;
    private BigDecimal fee;
    private BigDecimal totalAmount;
    private String ownerName;  // 红人关联的负责人
}
