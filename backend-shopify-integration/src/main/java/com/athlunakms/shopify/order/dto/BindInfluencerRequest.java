package com.athlunakms.shopify.order.dto;

public class BindInfluencerRequest {
    private String orderNo;
    private Long orderId;
    private Long shopifyOrderNumber;
    private Long influencerId;
    private String influencerName;

    public String getOrderNo() {
        return this.orderNo;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public Long getShopifyOrderNumber() {
        return this.shopifyOrderNumber;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setShopifyOrderNumber(Long shopifyOrderNumber) {
        this.shopifyOrderNumber = shopifyOrderNumber;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BindInfluencerRequest)) {
            return false;
        }
        BindInfluencerRequest other = (BindInfluencerRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$orderId = this.getOrderId();
        Long other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !((Object)this$orderId).equals(other$orderId)) {
            return false;
        }
        Long this$shopifyOrderNumber = this.getShopifyOrderNumber();
        Long other$shopifyOrderNumber = other.getShopifyOrderNumber();
        if (this$shopifyOrderNumber == null ? other$shopifyOrderNumber != null : !((Object)this$shopifyOrderNumber).equals(other$shopifyOrderNumber)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        String this$orderNo = this.getOrderNo();
        String other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        return !(this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BindInfluencerRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $orderId = this.getOrderId();
        result = result * 59 + ($orderId == null ? 43 : ((Object)$orderId).hashCode());
        Long $shopifyOrderNumber = this.getShopifyOrderNumber();
        result = result * 59 + ($shopifyOrderNumber == null ? 43 : ((Object)$shopifyOrderNumber).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        return result;
    }

    public String toString() {
        return "BindInfluencerRequest(orderNo=" + this.getOrderNo() + ", orderId=" + this.getOrderId() + ", shopifyOrderNumber=" + this.getShopifyOrderNumber() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ")";
    }
}

