package com.athlunakms.influencer.dto;

import lombok.Data;

@Data
public class RemittancePayDto {
    private String voucherUrl;
    private String remark;
    private String paymentMethod;
    private String paymentAccount;
    private String paymentDetails;
    private java.math.BigDecimal amount;
    private java.math.BigDecimal fee;
    private java.math.BigDecimal totalAmount;
    private String paidAt; // 实际打款时间，格式 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
}
