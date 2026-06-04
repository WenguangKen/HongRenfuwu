package com.athlunakms.influencer.dto;

import com.athlunakms.influencer.entity.CommissionPayout;
import org.springframework.beans.BeanUtils;

public class CommissionPayoutDto
extends CommissionPayout {
    private String ownerName;
    private String liaisonName;

    public CommissionPayoutDto() {
    }

    public CommissionPayoutDto(CommissionPayout payout) {
        if (payout != null) {
            BeanUtils.copyProperties((Object)payout, (Object)this);
        }
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public Long getInfluencerId() {
        return super.getInfluencerId();
    }

    public String getLiaisonName() {
        return this.liaisonName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setInfluencerId(Long influencerId) {
        super.setInfluencerId(influencerId);
    }

    public void setLiaisonName(String liaisonName) {
        this.liaisonName = liaisonName;
    }

    public String toString() {
        return "CommissionPayoutDto(ownerName=" + this.getOwnerName() + ", liaisonName=" + this.getLiaisonName() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CommissionPayoutDto)) {
            return false;
        }
        CommissionPayoutDto other = (CommissionPayoutDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$ownerName = this.getOwnerName();
        String other$ownerName = other.getOwnerName();
        if (this$ownerName == null ? other$ownerName != null : !this$ownerName.equals(other$ownerName)) {
            return false;
        }
        String this$liaisonName = this.getLiaisonName();
        String other$liaisonName = other.getLiaisonName();
        return !(this$liaisonName == null ? other$liaisonName != null : !this$liaisonName.equals(other$liaisonName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CommissionPayoutDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $ownerName = this.getOwnerName();
        result = result * 59 + ($ownerName == null ? 43 : $ownerName.hashCode());
        String $liaisonName = this.getLiaisonName();
        result = result * 59 + ($liaisonName == null ? 43 : $liaisonName.hashCode());
        return result;
    }
}

