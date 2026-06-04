package com.athlunakms.shopify.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="order_transactions")
public class OrderTransaction {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id", nullable=false)
    private Long orderId;
    @Column(name="shopify_transaction_id", nullable=false, unique=true, length=100)
    private String shopifyTransactionId;
    @Column(name="parent_transaction_id")
    private Long parentTransactionId;
    @Column(name="created_at_shopify")
    private LocalDateTime createdAtShopify;
    @Column(length=50)
    private String kind;
    @Column(length=50)
    private String status;
    @Column(length=100)
    private String gateway;
    @Column(precision=12, scale=2)
    private BigDecimal amount;
    @Column(length=10)
    private String currency;
    @Column(name="authorization_code", length=100)
    private String authorizationCode;
    @Column(name="payment_id", length=100)
    private String paymentId;
    @Column(name="receipt_json", columnDefinition="JSON")
    private String receiptJson;
    @Column(name="error_code", length=100)
    private String errorCode;
    @Column(columnDefinition="TEXT")
    private String message;
    @Column(name="sync_at")
    private LocalDateTime syncAt;

    @PrePersist
    @PreUpdate
    public void onSync() {
        this.syncAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getShopifyTransactionId() {
        return this.shopifyTransactionId;
    }

    public Long getParentTransactionId() {
        return this.parentTransactionId;
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

    public String getPaymentId() {
        return this.paymentId;
    }

    public String getReceiptJson() {
        return this.receiptJson;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getSyncAt() {
        return this.syncAt;
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

    public void setParentTransactionId(Long parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
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

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setReceiptJson(String receiptJson) {
        this.receiptJson = receiptJson;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSyncAt(LocalDateTime syncAt) {
        this.syncAt = syncAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderTransaction)) {
            return false;
        }
        OrderTransaction other = (OrderTransaction)o;
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
        Long this$parentTransactionId = this.getParentTransactionId();
        Long other$parentTransactionId = other.getParentTransactionId();
        if (this$parentTransactionId == null ? other$parentTransactionId != null : !((Object)this$parentTransactionId).equals(other$parentTransactionId)) {
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
        String this$paymentId = this.getPaymentId();
        String other$paymentId = other.getPaymentId();
        if (this$paymentId == null ? other$paymentId != null : !this$paymentId.equals(other$paymentId)) {
            return false;
        }
        String this$receiptJson = this.getReceiptJson();
        String other$receiptJson = other.getReceiptJson();
        if (this$receiptJson == null ? other$receiptJson != null : !this$receiptJson.equals(other$receiptJson)) {
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
        LocalDateTime this$syncAt = this.getSyncAt();
        LocalDateTime other$syncAt = other.getSyncAt();
        return !(this$syncAt == null ? other$syncAt != null : !((Object)this$syncAt).equals(other$syncAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderTransaction;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Long $parentTransactionId = this.getParentTransactionId();
        result = result * 59 + ($parentTransactionId == null ? 43 : ((Object)$parentTransactionId).hashCode());
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
        String $paymentId = this.getPaymentId();
        result = result * 59 + ($paymentId == null ? 43 : $paymentId.hashCode());
        String $receiptJson = this.getReceiptJson();
        result = result * 59 + ($receiptJson == null ? 43 : $receiptJson.hashCode());
        String $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        LocalDateTime $syncAt = this.getSyncAt();
        result = result * 59 + ($syncAt == null ? 43 : ((Object)$syncAt).hashCode());
        return result;
    }

    public String toString() {
        return "OrderTransaction(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", shopifyTransactionId=" + this.getShopifyTransactionId() + ", parentTransactionId=" + this.getParentTransactionId() + ", createdAtShopify=" + this.getCreatedAtShopify() + ", kind=" + this.getKind() + ", status=" + this.getStatus() + ", gateway=" + this.getGateway() + ", amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", authorizationCode=" + this.getAuthorizationCode() + ", paymentId=" + this.getPaymentId() + ", receiptJson=" + this.getReceiptJson() + ", errorCode=" + this.getErrorCode() + ", message=" + this.getMessage() + ", syncAt=" + this.getSyncAt() + ")";
    }
}

