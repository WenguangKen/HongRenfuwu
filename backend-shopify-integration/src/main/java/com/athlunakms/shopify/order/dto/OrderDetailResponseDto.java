package com.athlunakms.shopify.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDto {
    private Long id;
    private String orderNo;
    private String orderName;
    private String shopifyOrderId;
    private BigDecimal totalAmount;
    private BigDecimal totalShipping;
    private String financialStatus;
    private String fulfillmentStatus;
    private String storeName;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String customerName;
    private String customerEmail;
    private String shippingCountry;
    private String trackingNumber;
    private String trackingCompany;
    private String trackingUrl;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime updatedAt;
    private List<ProductInfo> products;
    private List<OrderPaymentDto> payments;
    private List<OrderRefundDto> refunds;
    private DiscountInfo discount;
    private List<OrderDetailLogDto> logs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Long id;
        private String name;
        private String title;
        private String sku;
        private String spu;
        private String variantTitle;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalAmount;
        private String imageUrl;
        private String image;
        private String status;
        private Long shopifyVariantId;
        private Long shopifyProductId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountInfo {
        private String code;
        private BigDecimal amount;
        private String type;
        private BigDecimal voucher;
    }
}


