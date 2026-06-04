package com.athlunakms.influencer.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RemittanceCreateDto {
    private Long influencerId;
    private String orderNo;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentType;
    private String remark;
    private String status;
    private String auditorName;
    private String payerName;
    private String paymentMethod;
    private String paymentAccount;
    private String paymentDetails;
}
