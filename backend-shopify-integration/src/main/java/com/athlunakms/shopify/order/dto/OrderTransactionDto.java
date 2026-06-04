package com.athlunakms.shopify.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderTransactionDto {
    private Long id;
    private Long orderId;
    private String shopifyTransactionId;
    private LocalDateTime createdAtShopify;
    private String kind;
    private String status;
    private String gateway;
    private BigDecimal amount;
    private String currency;
    private String authorizationCode;
    private String errorCode;
    private String message;
    private String kindDisplay;
    private String statusDisplay;

    public Long getId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getShopifyTransactionId() {
        return this.shopifyTransactionId;
    }

    public LocalDateTime getCreatedAtShopify() {
        return this.createdAtShopify;
    }

    public String getKind() {
        return this.kind;
    }

    public String getStatus() {
        return this.status;
    }

    public String getGateway() {
        return this.gateway;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getKindDisplay() {
        return this.kindDisplay;
    }

    public String getStatusDisplay() {
        return this.statusDisplay;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setShopifyTransactionId(String shopifyTransactionId) {
        this.shopifyTransactionId = shopifyTransactionId;
    }

    public void setCreatedAtShopify(LocalDateTime createdAtShopify) {
        this.createdAtShopify = createdAtShopify;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setKindDisplay(String kindDisplay) {
        this.kindDisplay = kindDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderTransactionDto)) {
            return false;
        }
        OrderTransactionDto other = (OrderTransactionDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$orderId = this.getOrderId();
        Long other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !((Object)this$orderId).equals(other$orderId)) {
            return false;
        }
        String this$shopifyTransactionId = this.getShopifyTransactionId();
        String other$shopifyTransactionId = other.getShopifyTransactionId();
        if (this$shopifyTransactionId == null ? other$shopifyTransactionId != null : !this$shopifyTransactionId.equals(other$shopifyTransactionId)) {
            return false;
        }
        LocalDateTime this$createdAtShopify = this.getCreatedAtShopify();
        LocalDateTime other$createdAtShopify = other.getCreatedAtShopify();
        if (this$createdAtShopify == null ? other$createdAtShopify != null : !((Object)this$createdAtShopify).equals(other$createdAtShopify)) {
            return false;
        }
        String this$kind = this.getKind();
        String other$kind = other.getKind();
        if (this$kind == null ? other$kind != null : !this$kind.equals(other$kind)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$gateway = this.getGateway();
        String other$gateway = other.getGateway();
        if (this$gateway == null ? other$gateway != null : !this$gateway.equals(other$gateway)) {
            return false;
        }
        BigDecimal this$amount = this.getAmount();
        BigDecimal other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !((Object)this$amount).equals(other$amount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$authorizationCode = this.getAuthorizationCode();
        String other$authorizationCode = other.getAuthorizationCode();
        if (this$authorizationCode == null ? other$authorizationCode != null : !this$authorizationCode.equals(other$authorizationCode)) {
            return false;
        }
        String this$errorCode = this.getErrorCode();
        String other$errorCode = other.getErrorCode();
        if (this$errorCode == null ? other$errorCode != null : !this$errorCode.equals(other$errorCode)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        String this$kindDisplay = this.getKindDisplay();
        String other$kindDisplay = other.getKindDisplay();
        if (this$kindDisplay == null ? other$kindDisplay != null : !this$kindDisplay.equals(other$kindDisplay)) {
            return false;
        }
        String this$statusDisplay = this.getStatusDisplay();
        String other$statusDisplay = other.getStatusDisplay();
        return !(this$statusDisplay == null ? other$statusDisplay != null : !this$statusDisplay.equals(other$statusDisplay));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderTransactionDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        String $shopifyTransactionId = this.getShopifyTransactionId();
        result = result * 59 + ($shopifyTransactionId == null ? 43 : $shopifyTransactionId.hashCode());
        LocalDateTime $createdAtShopify = this.getCreatedAtShopify();
        result = result * 59 + ($createdAtShopify == null ? 43 : ((Object)$createdAtShopify).hashCode());
        String $kind = this.getKind();
        result = result * 59 + ($kind == null ? 43 : $kind.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $gateway = this.getGateway();
        result = result * 59 + ($gateway == null ? 43 : $gateway.hashCode());
        BigDecimal $amount = this.getAmount();
        result = result * 59 + ($amount == null ? 43 : ((Object)$amount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $authorizationCode = this.getAuthorizationCode();
        result = result * 59 + ($authorizationCode == null ? 43 : $authorizationCode.hashCode());
        String $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        String $kindDisplay = this.getKindDisplay();
        result = result * 59 + ($kindDisplay == null ? 43 : $kindDisplay.hashCode());
        String $statusDisplay = this.getStatusDisplay();
        result = result * 59 + ($statusDisplay == null ? 43 : $statusDisplay.hashCode());
        return result;
    }

    public String toString() {
        return "OrderTransactionDto(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyTransactionId=" + this.getShopifyTransactionId() + ", createdAtShopify=" + this.getCreatedAtShopify() + ", kind=" + this.getKind() + ", status=" + this.getStatus() + ", gateway=" + this.getGateway() + ", amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", authorizationCode=" + this.getAuthorizationCode() + ", errorCode=" + this.getErrorCode() + ", message=" + this.getMessage() + ", kindDisplay=" + this.getKindDisplay() + ", statusDisplay=" + this.getStatusDisplay() + ")";
    }
}

