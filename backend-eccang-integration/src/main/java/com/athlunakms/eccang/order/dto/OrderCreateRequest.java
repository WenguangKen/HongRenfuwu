package com.athlunakms.eccang.order.dto;

import com.athlunakms.eccang.order.dto.OrderCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private Long storeId;
    private String customerEmail;
    private String customerPhone;
    @Builder.Default
    private String currency = "USD";
    private List<OrderLineItemRequest> lineItems;
    private AddressRequest shippingAddress;
    private AddressRequest billingAddress;
    @Builder.Default
    private String financialStatus = "PENDING";
    @Builder.Default
    private String fulfillmentStatus = "UNFULFILLED";
    private String discountCode;
    private ShippingLineRequest shippingLine;
    private List<TransactionRequest> transactions;
    private String note;
    private String tags;
    @Builder.Default
    private boolean sendReceipt = false;
    @Builder.Default
    private boolean sendFulfillmentReceipt = false;
    private String orderSource;
    private Long influencerId;
    private String influencerDiscountCode;
    @Builder.Default
    private Boolean isDraft = false;
    @Builder.Default
    private Boolean isFbaShipment = false;
    private String fbaWarehouseCode;
    private String fbaShippingMethod;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        private String firstName;
        private String lastName;
        private String address1;
        private String address2;
        private String city;
        private String province;
        private String provinceCode;
        private String country;
        private String countryCode;
        private String zip;
        private String phone;
        private String company;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLineItemRequest {
        private Long variantId;
        private String eccangVariantId;
        private String eccangVariantGid;
        private Integer quantity;
        private BigDecimal priceOverride;
        private Boolean requiresShipping;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingLineRequest {
        private String title;
        private BigDecimal price;
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionRequest {
        private String kind;
        private String status;
        private BigDecimal amount;
        private String currency;
        private String gateway;
    }
}

