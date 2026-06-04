package com.athlunakms.influencer.dto;

import java.math.BigDecimal;

public class InfluencerRankDto {
    private Long influencerId;
    private String name;
    private BigDecimal gmv;
    private Long orderCount;

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getGmv() {
        return this.gmv;
    }

    public Long getOrderCount() {
        return this.orderCount;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGmv(BigDecimal gmv) {
        this.gmv = gmv;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerRankDto)) {
            return false;
        }
        InfluencerRankDto other = (InfluencerRankDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Long this$orderCount = this.getOrderCount();
        Long other$orderCount = other.getOrderCount();
        if (this$orderCount == null ? other$orderCount != null : !((Object)this$orderCount).equals(other$orderCount)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        BigDecimal this$gmv = this.getGmv();
        BigDecimal other$gmv = other.getGmv();
        return !(this$gmv == null ? other$gmv != null : !((Object)this$gmv).equals(other$gmv));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerRankDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $orderCount = this.getOrderCount();
        result = result * 59 + ($orderCount == null ? 43 : ((Object)$orderCount).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        BigDecimal $gmv = this.getGmv();
        result = result * 59 + ($gmv == null ? 43 : ((Object)$gmv).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerRankDto(influencerId=" + this.getInfluencerId() + ", name=" + this.getName() + ", gmv=" + this.getGmv() + ", orderCount=" + this.getOrderCount() + ")";
    }

    public InfluencerRankDto(Long influencerId, String name, BigDecimal gmv, Long orderCount) {
        this.influencerId = influencerId;
        this.name = name;
        this.gmv = gmv;
        this.orderCount = orderCount;
    }

    public InfluencerRankDto() {
    }
}

