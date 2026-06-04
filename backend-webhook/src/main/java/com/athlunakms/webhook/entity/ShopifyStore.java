package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="shopify_stores")
public class ShopifyStore {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="shop_domain")
    private String shopDomain;
    @Column(name="myshopify_domain")
    private String myshopifyDomain;
    @Column(name="store_name")
    private String storeName;

    public Long getId() {
        return this.id;
    }

    public String getShopDomain() {
        return this.shopDomain;
    }

    public String getMyshopifyDomain() {
        return this.myshopifyDomain;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain;
    }

    public void setMyshopifyDomain(String myshopifyDomain) {
        this.myshopifyDomain = myshopifyDomain;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyStore)) {
            return false;
        }
        ShopifyStore other = (ShopifyStore)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$shopDomain = this.getShopDomain();
        String other$shopDomain = other.getShopDomain();
        if (this$shopDomain == null ? other$shopDomain != null : !this$shopDomain.equals(other$shopDomain)) {
            return false;
        }
        String this$myshopifyDomain = this.getMyshopifyDomain();
        String other$myshopifyDomain = other.getMyshopifyDomain();
        if (this$myshopifyDomain == null ? other$myshopifyDomain != null : !this$myshopifyDomain.equals(other$myshopifyDomain)) {
            return false;
        }
        String this$storeName = this.getStoreName();
        String other$storeName = other.getStoreName();
        return !(this$storeName == null ? other$storeName != null : !this$storeName.equals(other$storeName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyStore;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $shopDomain = this.getShopDomain();
        result = result * 59 + ($shopDomain == null ? 43 : $shopDomain.hashCode());
        String $myshopifyDomain = this.getMyshopifyDomain();
        result = result * 59 + ($myshopifyDomain == null ? 43 : $myshopifyDomain.hashCode());
        String $storeName = this.getStoreName();
        result = result * 59 + ($storeName == null ? 43 : $storeName.hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyStore(id=" + this.getId() + ", shopDomain=" + this.getShopDomain() + ", myshopifyDomain=" + this.getMyshopifyDomain() + ", storeName=" + this.getStoreName() + ")";
    }
}

