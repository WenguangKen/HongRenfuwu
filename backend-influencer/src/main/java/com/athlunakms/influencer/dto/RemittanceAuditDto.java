package com.athlunakms.influencer.dto;

import lombok.Data;

@Data
public class RemittanceAuditDto {
    private String action; // approve or reject
    private String remark;
    private String paymentMethod;
    private String paymentAccount;
    private String paymentDetails;
    private java.math.BigDecimal amount;
    private java.math.BigDecimal fee;
    private java.math.BigDecimal totalAmount;
}
