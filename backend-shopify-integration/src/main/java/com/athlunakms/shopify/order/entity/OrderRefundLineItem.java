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
@Table(name="order_refund_line_items")
public class OrderRefundLineItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="refund_id", nullable=false)
    private Long refundId;
    @Column(name="order_line_item_id")
    private Long orderLineItemId;
    @Column(name="shopify_refund_line_id", length=100)
    private String shopifyRefundLineId;
    @Column(name="shopify_order_line_item_id", length=100)
    private String shopifyOrderLineItemId;
    @Column(name="quantity")
    private Integer quantity;
    @Column(name="subtotal", precision=12, scale=2)
    private BigDecimal subtotal;
    @Column(name="tax_amount", precision=12, scale=2)
    private BigDecimal taxAmount;
    @Column(name="currency", length=10)
    private String currency;
    @Column(name="presentment_subtotal", precision=12, scale=2)
    private BigDecimal presentmentSubtotal;
    @Column(name="presentment_tax_amount", precision=12, scale=2)
    private BigDecimal presentmentTaxAmount;
    @Column(name="presentment_currency", length=10)
    private String presentmentCurrency;
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

    public Long getRefundId() {
        return this.refundId;
    }

    public Long getOrderLineItemId() {
        return this.orderLineItemId;
    }

    public String getShopifyRefundLineId() {
        return this.shopifyRefundLineId;
    }

    public String getShopifyOrderLineItemId() {
        return this.shopifyOrderLineItemId;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public BigDecimal getTaxAmount() {
        return this.taxAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getPresentmentSubtotal() {
        return this.presentmentSubtotal;
    }

    public BigDecimal getPresentmentTaxAmount() {
        return this.presentmentTaxAmount;
    }

    public String getPresentmentCurrency() {
        return this.presentmentCurrency;
    }

    public LocalDateTime getSyncAt() {
        return this.syncAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public void setOrderLineItemId(Long orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public void setShopifyRefundLineId(String shopifyRefundLineId) {
        this.shopifyRefundLineId = shopifyRefundLineId;
    }

    public void setShopifyOrderLineItemId(String shopifyOrderLineItemId) {
        this.shopifyOrderLineItemId = shopifyOrderLineItemId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPresentmentSubtotal(BigDecimal presentmentSubtotal) {
        this.presentmentSubtotal = presentmentSubtotal;
    }

    public void setPresentmentTaxAmount(BigDecimal presentmentTaxAmount) {
        this.presentmentTaxAmount = presentmentTaxAmount;
    }

    public void setPresentmentCurrency(String presentmentCurrency) {
        this.presentmentCurrency = presentmentCurrency;
    }

    public void setSyncAt(LocalDateTime syncAt) {
        this.syncAt = syncAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderRefundLineItem)) {
            return false;
        }
        OrderRefundLineItem other = (OrderRefundLineItem)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$refundId = this.getRefundId();
        Long other$refundId = other.getRefundId();
        if (this$refundId == null ? other$refundId != null : !((Object)this$refundId).equals(other$refundId)) {
            return false;
        }
        Long this$orderLineItemId = this.getOrderLineItemId();
        Long other$orderLineItemId = other.getOrderLineItemId();
        if (this$orderLineItemId == null ? other$orderLineItemId != null : !((Object)this$orderLineItemId).equals(other$orderLineItemId)) {
            return false;
        }
        Integer this$quantity = this.getQuantity();
        Integer other$quantity = other.getQuantity();
        if (this$quantity == null ? other$quantity != null : !((Object)this$quantity).equals(other$quantity)) {
            return false;
        }
        String this$shopifyRefundLineId = this.getShopifyRefundLineId();
        String other$shopifyRefundLineId = other.getShopifyRefundLineId();
        if (this$shopifyRefundLineId == null ? other$shopifyRefundLineId != null : !this$shopifyRefundLineId.equals(other$shopifyRefundLineId)) {
            return false;
        }
        String this$shopifyOrderLineItemId = this.getShopifyOrderLineItemId();
        String other$shopifyOrderLineItemId = other.getShopifyOrderLineItemId();
        if (this$shopifyOrderLineItemId == null ? other$shopifyOrderLineItemId != null : !this$shopifyOrderLineItemId.equals(other$shopifyOrderLineItemId)) {
            return false;
        }
        BigDecimal this$subtotal = this.getSubtotal();
        BigDecimal other$subtotal = other.getSubtotal();
        if (this$subtotal == null ? other$subtotal != null : !((Object)this$subtotal).equals(other$subtotal)) {
            return false;
        }
        BigDecimal this$taxAmount = this.getTaxAmount();
        BigDecimal other$taxAmount = other.getTaxAmount();
        if (this$taxAmount == null ? other$taxAmount != null : !((Object)this$taxAmount).equals(other$taxAmount)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        BigDecimal this$presentmentSubtotal = this.getPresentmentSubtotal();
        BigDecimal other$presentmentSubtotal = other.getPresentmentSubtotal();
        if (this$presentmentSubtotal == null ? other$presentmentSubtotal != null : !((Object)this$presentmentSubtotal).equals(other$presentmentSubtotal)) {
            return false;
        }
        BigDecimal this$presentmentTaxAmount = this.getPresentmentTaxAmount();
        BigDecimal other$presentmentTaxAmount = other.getPresentmentTaxAmount();
        if (this$presentmentTaxAmount == null ? other$presentmentTaxAmount != null : !((Object)this$presentmentTaxAmount).equals(other$presentmentTaxAmount)) {
            return false;
        }
        String this$presentmentCurrency = this.getPresentmentCurrency();
        String other$presentmentCurrency = other.getPresentmentCurrency();
        if (this$presentmentCurrency == null ? other$presentmentCurrency != null : !this$presentmentCurrency.equals(other$presentmentCurrency)) {
            return false;
        }
        LocalDateTime this$syncAt = this.getSyncAt();
        LocalDateTime other$syncAt = other.getSyncAt();
        return !(this$syncAt == null ? other$syncAt != null : !((Object)this$syncAt).equals(other$syncAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderRefundLineItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $refundId = this.getRefundId();
        result = result * 59 + ($refundId == null ? 43 : ((Object)$refundId).hashCode());
        Long $orderLineItemId = this.getOrderLineItemId();
        result = result * 59 + ($orderLineItemId == null ? 43 : ((Object)$orderLineItemId).hashCode());
        Integer $quantity = this.getQuantity();
        result = result * 59 + ($quantity == null ? 43 : ((Object)$quantity).hashCode());
        String $shopifyRefundLineId = this.getShopifyRefundLineId();
        result = result * 59 + ($shopifyRefundLineId == null ? 43 : $shopifyRefundLineId.hashCode());
        String $shopifyOrderLineItemId = this.getShopifyOrderLineItemId();
        result = result * 59 + ($shopifyOrderLineItemId == null ? 43 : $shopifyOrderLineItemId.hashCode());
        BigDecimal $subtotal = this.getSubtotal();
        result = result * 59 + ($subtotal == null ? 43 : ((Object)$subtotal).hashCode());
        BigDecimal $taxAmount = this.getTaxAmount();
        result = result * 59 + ($taxAmount == null ? 43 : ((Object)$taxAmount).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        BigDecimal $presentmentSubtotal = this.getPresentmentSubtotal();
        result = result * 59 + ($presentmentSubtotal == null ? 43 : ((Object)$presentmentSubtotal).hashCode());
        BigDecimal $presentmentTaxAmount = this.getPresentmentTaxAmount();
        result = result * 59 + ($presentmentTaxAmount == null ? 43 : ((Object)$presentmentTaxAmount).hashCode());
        String $presentmentCurrency = this.getPresentmentCurrency();
        result = result * 59 + ($presentmentCurrency == null ? 43 : $presentmentCurrency.hashCode());
        LocalDateTime $syncAt = this.getSyncAt();
        result = result * 59 + ($syncAt == null ? 43 : ((Object)$syncAt).hashCode());
        return result;
    }

    public String toString() {
        return "OrderRefundLineItem(id=" + this.getId() + ", refundId=" + this.getRefundId() + ", orderLineItemId=" + this.getOrderLineItemId() + ", shopifyRefundLineId=" + this.getShopifyRefundLineId() + ", shopifyOrderLineItemId=" + this.getShopifyOrderLineItemId() + ", quantity=" + this.getQuantity() + ", subtotal=" + this.getSubtotal() + ", taxAmount=" + this.getTaxAmount() + ", currency=" + this.getCurrency() + ", presentmentSubtotal=" + this.getPresentmentSubtotal() + ", presentmentTaxAmount=" + this.getPresentmentTaxAmount() + ", presentmentCurrency=" + this.getPresentmentCurrency() + ", syncAt=" + this.getSyncAt() + ")";
    }
}

