package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="commission_setting")
public class CommissionSetting {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private Boolean enabled = true;
    @Column(name="order_status", nullable=false, length=50)
    private String orderStatus = "delivered";
    @Column(name="wait_days", nullable=false)
    private Integer waitDays = 7;
    @Column(name="check_refund_days", nullable=false)
    private Integer checkRefundDays = 3;
    @Column(name="only_unsettled", nullable=false)
    private Boolean onlyUnsettled = true;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public Integer getWaitDays() {
        return this.waitDays;
    }

    public Integer getCheckRefundDays() {
        return this.checkRefundDays;
    }

    public Boolean getOnlyUnsettled() {
        return this.onlyUnsettled;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setWaitDays(Integer waitDays) {
        this.waitDays = waitDays;
    }

    public void setCheckRefundDays(Integer checkRefundDays) {
        this.checkRefundDays = checkRefundDays;
    }

    public void setOnlyUnsettled(Boolean onlyUnsettled) {
        this.onlyUnsettled = onlyUnsettled;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CommissionSetting)) {
            return false;
        }
        CommissionSetting other = (CommissionSetting)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Boolean this$enabled = this.getEnabled();
        Boolean other$enabled = other.getEnabled();
        if (this$enabled == null ? other$enabled != null : !((Object)this$enabled).equals(other$enabled)) {
            return false;
        }
        Integer this$waitDays = this.getWaitDays();
        Integer other$waitDays = other.getWaitDays();
        if (this$waitDays == null ? other$waitDays != null : !((Object)this$waitDays).equals(other$waitDays)) {
            return false;
        }
        Integer this$checkRefundDays = this.getCheckRefundDays();
        Integer other$checkRefundDays = other.getCheckRefundDays();
        if (this$checkRefundDays == null ? other$checkRefundDays != null : !((Object)this$checkRefundDays).equals(other$checkRefundDays)) {
            return false;
        }
        Boolean this$onlyUnsettled = this.getOnlyUnsettled();
        Boolean other$onlyUnsettled = other.getOnlyUnsettled();
        if (this$onlyUnsettled == null ? other$onlyUnsettled != null : !((Object)this$onlyUnsettled).equals(other$onlyUnsettled)) {
            return false;
        }
        String this$orderStatus = this.getOrderStatus();
        String other$orderStatus = other.getOrderStatus();
        if (this$orderStatus == null ? other$orderStatus != null : !this$orderStatus.equals(other$orderStatus)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        return !(this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CommissionSetting;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Boolean $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : ((Object)$enabled).hashCode());
        Integer $waitDays = this.getWaitDays();
        result = result * 59 + ($waitDays == null ? 43 : ((Object)$waitDays).hashCode());
        Integer $checkRefundDays = this.getCheckRefundDays();
        result = result * 59 + ($checkRefundDays == null ? 43 : ((Object)$checkRefundDays).hashCode());
        Boolean $onlyUnsettled = this.getOnlyUnsettled();
        result = result * 59 + ($onlyUnsettled == null ? 43 : ((Object)$onlyUnsettled).hashCode());
        String $orderStatus = this.getOrderStatus();
        result = result * 59 + ($orderStatus == null ? 43 : $orderStatus.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "CommissionSetting(id=" + this.getId() + ", enabled=" + this.getEnabled() + ", orderStatus=" + this.getOrderStatus() + ", waitDays=" + this.getWaitDays() + ", checkRefundDays=" + this.getCheckRefundDays() + ", onlyUnsettled=" + this.getOnlyUnsettled() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }

    public CommissionSetting() {
    }

    public CommissionSetting(Long id, Boolean enabled, String orderStatus, Integer waitDays, Integer checkRefundDays, Boolean onlyUnsettled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.enabled = enabled;
        this.orderStatus = orderStatus;
        this.waitDays = waitDays;
        this.checkRefundDays = checkRefundDays;
        this.onlyUnsettled = onlyUnsettled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

