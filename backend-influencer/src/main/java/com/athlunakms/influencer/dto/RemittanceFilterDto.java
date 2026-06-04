package com.athlunakms.influencer.dto;

import lombok.Data;

@Data
public class RemittanceFilterDto {
    private String taskNo;
    private Long influencerId;
    private String status;
    private Integer page;
    private Integer size;
    // 新增筛选
    private String paymentMethod;
    private String auditorName;
    private String payerName;
    private String ownerName;
}
