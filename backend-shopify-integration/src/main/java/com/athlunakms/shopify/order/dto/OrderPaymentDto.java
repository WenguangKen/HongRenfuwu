package com.athlunakms.shopify.order.dto;

import lombok.Data;

@Data
public class OrderPaymentDto {
    private String id;
    private String paymentNo;
    private String time;
    private String amount;
    private String method;
    private String transactionId;
    private String status;
    private String operator;
}
