package com.athlunakms.eccang.order.dto;

public class BindInfluencerRequest {
    private String orderNo;
    private Long orderId;
    private Long eccangOrderNumber;
    private Long influencerId;
    private String influencerName;

    public String getOrderNo() {
        return this.orderNo;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public Long getEccangOrderNumber() {
        return this.eccangOrderNumber;
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

    public void setEccangOrderNumber(Long eccangOrderNumber) {
        this.eccangOrderNumber = eccangOrderNumber;
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
        Long this$eccangOrderNumber = this.getEccangOrderNumber();
        Long other$eccangOrderNumber = other.getEccangOrderNumber();
        if (this$eccangOrderNumber == null ? other$eccangOrderNumber != null : !((Object)this$eccangOrderNumber).equals(other$eccangOrderNumber)) {
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
        Long $eccangOrderNumber = this.getEccangOrderNumber();
        result = result * 59 + ($eccangOrderNumber == null ? 43 : ((Object)$eccangOrderNumber).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $orderNo = this.getOrderNo();
        result = result * 59 + ($orderNo == null ? 43 : $orderNo.hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        return result;
    }

    public String toString() {
        return "BindInfluencerRequest(orderNo=" + this.getOrderNo() + ", orderId=" + this.getOrderId() + ", eccangOrderNumber=" + this.getEccangOrderNumber() + ", influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ")";
    }
}

