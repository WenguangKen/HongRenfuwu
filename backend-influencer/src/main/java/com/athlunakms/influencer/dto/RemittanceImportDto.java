package com.athlunakms.influencer.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RemittanceImportDto {
    private String email;
    private String handle;
    private String paymentType;
    private BigDecimal amount;
    private String currency;
    private String orderNo;
    private String remark;
    private boolean matchSuccess;
    private String matchErrorMessage;
    private Long matchedInfluencerId;
    private String matchedInfluencerName;
}
