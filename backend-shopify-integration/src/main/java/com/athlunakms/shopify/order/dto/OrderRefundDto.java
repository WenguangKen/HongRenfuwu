package com.athlunakms.shopify.order.dto;

import lombok.Data;

@Data
public class OrderRefundDto {
    private String id;
    private String refundNo;
    private String time;
    private String amount;
    private String reason;
    private String method;
    private String status;
    private String operator;
}
