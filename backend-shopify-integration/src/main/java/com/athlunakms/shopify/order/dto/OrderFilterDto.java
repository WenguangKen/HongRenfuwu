package com.athlunakms.shopify.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单筛选查询 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterDto {
    private String orderNo;
    private String shopifyOrderId;
    private String influencerName;
    private String logisticsStatus;
    private String tab;
    private String packageNo;
    private String trackingNo;
    private String spu;
    private String timeType;
    private String startTime;
    private String endTime;
    private String influencerSocialSearch;
    private String recipientName;
    private String recipientCountry;
    private String recipientCity;
    private String customerEmail;
    private List<Long> ownerId;
    private List<Long> contactPersonId;
    private Long influencerId;
    private String discountCode;
    private String commissionStatus;
}
