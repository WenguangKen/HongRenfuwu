package com.athlunakms.shopify.order.dto;

public class OrderCreateResponse {
    private boolean success;
    private String message;
    private String shopifyOrderId;
    private Long shopifyOrderNumber;
    private String orderName;
    private String financialStatus;
    private String createdAt;
    private Long localOrderId;
    private String errorCode;
    private String errorField;

    public static OrderCreateResponse success(String shopifyOrderId, Long shopifyOrderNumber, String orderName, Long localOrderId) {
        OrderCreateResponse response = new OrderCreateResponse();
        response.setSuccess(true);
        response.setMessage("\u8ba2\u5355\u521b\u5efa\u6210\u529f");
        response.setShopifyOrderId(shopifyOrderId);
        response.setShopifyOrderNumber(shopifyOrderNumber);
        response.setOrderName(orderName);
        response.setLocalOrderId(localOrderId);
        return response;
    }

    public static OrderCreateResponse error(String message, String errorField) {
        OrderCreateResponse response = new OrderCreateResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorField(errorField);
        return response;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public Long getShopifyOrderNumber() {
        return this.shopifyOrderNumber;
    }

    public String getOrderName() {
        return this.orderName;
    }

    public String getFinancialStatus() {
        return this.financialStatus;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public Long getLocalOrderId() {
        return this.localOrderId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorField() {
        return this.errorField;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setShopifyOrderNumber(Long shopifyOrderNumber) {
        this.shopifyOrderNumber = shopifyOrderNumber;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setLocalOrderId(Long localOrderId) {
        this.localOrderId = localOrderId;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorField(String errorField) {
        this.errorField = errorField;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderCreateResponse)) {
            return false;
        }
        OrderCreateResponse other = (OrderCreateResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        Long this$shopifyOrderNumber = this.getShopifyOrderNumber();
        Long other$shopifyOrderNumber = other.getShopifyOrderNumber();
        if (this$shopifyOrderNumber == null ? other$shopifyOrderNumber != null : !((Object)this$shopifyOrderNumber).equals(other$shopifyOrderNumber)) {
            return false;
        }
        Long this$localOrderId = this.getLocalOrderId();
        Long other$localOrderId = other.getLocalOrderId();
        if (this$localOrderId == null ? other$localOrderId != null : !((Object)this$localOrderId).equals(other$localOrderId)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
            return false;
        }
        String this$orderName = this.getOrderName();
        String other$orderName = other.getOrderName();
        if (this$orderName == null ? other$orderName != null : !this$orderName.equals(other$orderName)) {
            return false;
        }
        String this$financialStatus = this.getFinancialStatus();
        String other$financialStatus = other.getFinancialStatus();
        if (this$financialStatus == null ? other$financialStatus != null : !this$financialStatus.equals(other$financialStatus)) {
            return false;
        }
        String this$createdAt = this.getCreatedAt();
        String other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) {
            return false;
        }
        String this$errorCode = this.getErrorCode();
        String other$errorCode = other.getErrorCode();
        if (this$errorCode == null ? other$errorCode != null : !this$errorCode.equals(other$errorCode)) {
            return false;
        }
        String this$errorField = this.getErrorField();
        String other$errorField = other.getErrorField();
        return !(this$errorField == null ? other$errorField != null : !this$errorField.equals(other$errorField));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderCreateResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        Long $shopifyOrderNumber = this.getShopifyOrderNumber();
        result = result * 59 + ($shopifyOrderNumber == null ? 43 : ((Object)$shopifyOrderNumber).hashCode());
        Long $localOrderId = this.getLocalOrderId();
        result = result * 59 + ($localOrderId == null ? 43 : ((Object)$localOrderId).hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $orderName = this.getOrderName();
        result = result * 59 + ($orderName == null ? 43 : $orderName.hashCode());
        String $financialStatus = this.getFinancialStatus();
        result = result * 59 + ($financialStatus == null ? 43 : $financialStatus.hashCode());
        String $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : $createdAt.hashCode());
        String $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        String $errorField = this.getErrorField();
        result = result * 59 + ($errorField == null ? 43 : $errorField.hashCode());
        return result;
    }

    public String toString() {
        return "OrderCreateResponse(success=" + this.isSuccess() + ", message=" + this.getMessage() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber() + ", orderName=" + this.getOrderName() + ", financialStatus=" + this.getFinancialStatus() + ", createdAt=" + this.getCreatedAt() + ", localOrderId=" + this.getLocalOrderId() + ", errorCode=" + this.getErrorCode() + ", errorField=" + this.getErrorField() + ")";
    }
}

