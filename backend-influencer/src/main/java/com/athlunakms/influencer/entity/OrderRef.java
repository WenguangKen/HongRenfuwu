package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="orders")
public class OrderRef {
    @Id
    private Long id;
    @Column(name="shopify_order_id")
    private String shopifyOrderId;
    @Column(name="order_number")
    private Long orderNumber;
    @Column(name="name")
    private String name;
    @Column(name="email")
    private String email;
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public String getShopifyOrderId() {
        return this.shopifyOrderId;
    }

    public Long getOrderNumber() {
        return this.orderNumber;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopifyOrderId(String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderRef)) {
            return false;
        }
        OrderRef other = (OrderRef)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$orderNumber = this.getOrderNumber();
        Long other$orderNumber = other.getOrderNumber();
        if (this$orderNumber == null ? other$orderNumber != null : !((Object)this$orderNumber).equals(other$orderNumber)) {
            return false;
        }
        String this$shopifyOrderId = this.getShopifyOrderId();
        String other$shopifyOrderId = other.getShopifyOrderId();
        if (this$shopifyOrderId == null ? other$shopifyOrderId != null : !this$shopifyOrderId.equals(other$shopifyOrderId)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderRef;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $orderNumber = this.getOrderNumber();
        result = result * 59 + ($orderNumber == null ? 43 : ((Object)$orderNumber).hashCode());
        String $shopifyOrderId = this.getShopifyOrderId();
        result = result * 59 + ($shopifyOrderId == null ? 43 : $shopifyOrderId.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "OrderRef(id=" + this.getId() + ", shopifyOrderId=" + this.getShopifyOrderId() + ", orderNumber=" + this.getOrderNumber() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

