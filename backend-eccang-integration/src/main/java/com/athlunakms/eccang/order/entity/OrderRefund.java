package com.athlunakms.eccang.order.entity;

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
@Table(name="order_refunds")
public class OrderRefund {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="order_id", nullable=false)
    private Long orderId;
    @Column(name="eccang_refund_id", nullable=false, unique=true, length=100)
    private String eccangRefundId;
    @Column(name="created_at_eccang")
    private LocalDateTime createdAtEccang;
    @Column(name="processed_at")
    private LocalDateTime processedAt;
    @Column(name="currency", length=10)
    private String currency;
    @Column(name="total_refunded", precision=12, scale=2)
    private BigDecimal totalRefunded;
    @Column(name="presentment_currency", length=10)
    private String presentmentCurrency;
    @Column(name="presentment_total_refunded", precision=12, scale=2)
    private BigDecimal presentmentTotalRefunded;
    @Column(name="note", columnDefinition="TEXT")
    private String note;
    @Column(name="restock")
    private Boolean restock;
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

    public String getEccangRefundId() {
        return this.eccangRefundId;
    }

    public LocalDateTime getCreatedAtEccang() {
        return this.createdAtEccang;
    }

    public LocalDateTime getProcessedAt() {
        return this.processedAt;
    }

    public String getCurrency() {
        return this.currency;
    }

    public BigDecimal getTotalRefunded() {
        return this.totalRefunded;
    }

    public String getPresentmentCurrency() {
        return this.presentmentCurrency;
    }

    public BigDecimal getPresentmentTotalRefunded() {
        return this.presentmentTotalRefunded;
    }

    public String getNote() {
        return this.note;
    }

    public Boolean getRestock() {
        return this.restock;
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

    public void setEccangRefundId(String eccangRefundId) {
        this.eccangRefundId = eccangRefundId;
    }

    public void setCreatedAtEccang(LocalDateTime createdAtEccang) {
        this.createdAtEccang = createdAtEccang;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTotalRefunded(BigDecimal totalRefunded) {
        this.totalRefunded = totalRefunded;
    }

    public void setPresentmentCurrency(String presentmentCurrency) {
        this.presentmentCurrency = presentmentCurrency;
    }

    public void setPresentmentTotalRefunded(BigDecimal presentmentTotalRefunded) {
        this.presentmentTotalRefunded = presentmentTotalRefunded;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setRestock(Boolean restock) {
        this.restock = restock;
    }

    public void setSyncAt(LocalDateTime syncAt) {
        this.syncAt = syncAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderRefund)) {
            return false;
        }
        OrderRefund other = (OrderRefund)o;
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
        Boolean this$restock = this.getRestock();
        Boolean other$restock = other.getRestock();
        if (this$restock == null ? other$restock != null : !((Object)this$restock).equals(other$restock)) {
            return false;
        }
        String this$eccangRefundId = this.getEccangRefundId();
        String other$eccangRefundId = other.getEccangRefundId();
        if (this$eccangRefundId == null ? other$eccangRefundId != null : !this$eccangRefundId.equals(other$eccangRefundId)) {
            return false;
        }
        LocalDateTime this$createdAtEccang = this.getCreatedAtEccang();
        LocalDateTime other$createdAtEccang = other.getCreatedAtEccang();
        if (this$createdAtEccang == null ? other$createdAtEccang != null : !((Object)this$createdAtEccang).equals(other$createdAtEccang)) {
            return false;
        }
        LocalDateTime this$processedAt = this.getProcessedAt();
        LocalDateTime other$processedAt = other.getProcessedAt();
        if (this$processedAt == null ? other$processedAt != null : !((Object)this$processedAt).equals(other$processedAt)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        BigDecimal this$totalRefunded = this.getTotalRefunded();
        BigDecimal other$totalRefunded = other.getTotalRefunded();
        if (this$totalRefunded == null ? other$totalRefunded != null : !((Object)this$totalRefunded).equals(other$totalRefunded)) {
            return false;
        }
        String this$presentmentCurrency = this.getPresentmentCurrency();
        String other$presentmentCurrency = other.getPresentmentCurrency();
        if (this$presentmentCurrency == null ? other$presentmentCurrency != null : !this$presentmentCurrency.equals(other$presentmentCurrency)) {
            return false;
        }
        BigDecimal this$presentmentTotalRefunded = this.getPresentmentTotalRefunded();
        BigDecimal other$presentmentTotalRefunded = other.getPresentmentTotalRefunded();
        if (this$presentmentTotalRefunded == null ? other$presentmentTotalRefunded != null : !((Object)this$presentmentTotalRefunded).equals(other$presentmentTotalRefunded)) {
            return false;
        }
        String this$note = this.getNote();
        String other$note = other.getNote();
        if (this$note == null ? other$note != null : !this$note.equals(other$note)) {
            return false;
        }
        LocalDateTime this$syncAt = this.getSyncAt();
        LocalDateTime other$syncAt = other.getSyncAt();
        return !(this$syncAt == null ? other$syncAt != null : !((Object)this$syncAt).equals(other$syncAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderRefund;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Boolean $restock = this.getRestock();
        result = result * 59 + ($restock == null ? 43 : ((Object)$restock).hashCode());
        String $eccangRefundId = this.getEccangRefundId();
        result = result * 59 + ($eccangRefundId == null ? 43 : $eccangRefundId.hashCode());
        LocalDateTime $createdAtEccang = this.getCreatedAtEccang();
        result = result * 59 + ($createdAtEccang == null ? 43 : ((Object)$createdAtEccang).hashCode());
        LocalDateTime $processedAt = this.getProcessedAt();
        result = result * 59 + ($processedAt == null ? 43 : ((Object)$processedAt).hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        BigDecimal $totalRefunded = this.getTotalRefunded();
        result = result * 59 + ($totalRefunded == null ? 43 : ((Object)$totalRefunded).hashCode());
        String $presentmentCurrency = this.getPresentmentCurrency();
        result = result * 59 + ($presentmentCurrency == null ? 43 : $presentmentCurrency.hashCode());
        BigDecimal $presentmentTotalRefunded = this.getPresentmentTotalRefunded();
        result = result * 59 + ($presentmentTotalRefunded == null ? 43 : ((Object)$presentmentTotalRefunded).hashCode());
        String $note = this.getNote();
        result = result * 59 + ($note == null ? 43 : $note.hashCode());
        LocalDateTime $syncAt = this.getSyncAt();
        result = result * 59 + ($syncAt == null ? 43 : ((Object)$syncAt).hashCode());
        return result;
    }

    public String toString() {
        return "OrderRefund(id=" + this.getId() + ", orderId=" + this.getOrderId() + ", eccangRefundId=" + this.getEccangRefundId() + ", createdAtEccang=" + this.getCreatedAtEccang() + ", processedAt=" + this.getProcessedAt() + ", currency=" + this.getCurrency() + ", totalRefunded=" + this.getTotalRefunded() + ", presentmentCurrency=" + this.getPresentmentCurrency() + ", presentmentTotalRefunded=" + this.getPresentmentTotalRefunded() + ", note=" + this.getNote() + ", restock=" + this.getRestock() + ", syncAt=" + this.getSyncAt() + ")";
    }
}

