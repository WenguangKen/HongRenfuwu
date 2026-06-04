package com.athlunakms.user.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private Long activeInfluencers;
    private Long conversionOrders;
    private BigDecimal orderGMV;
    private BigDecimal commissionAmount;
    private BigDecimal activeInfluencersTrend;
    private BigDecimal conversionOrdersTrend;
    private BigDecimal orderGMVTrend;
    private BigDecimal commissionTrend;
    private Long todayOrders;
    private BigDecimal todayGMV;
    private BigDecimal todaySettledCommission;

    public Long getActiveInfluencers() {
        return this.activeInfluencers;
    }

    public Long getConversionOrders() {
        return this.conversionOrders;
    }

    public BigDecimal getOrderGMV() {
        return this.orderGMV;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public BigDecimal getActiveInfluencersTrend() {
        return this.activeInfluencersTrend;
    }

    public BigDecimal getConversionOrdersTrend() {
        return this.conversionOrdersTrend;
    }

    public BigDecimal getOrderGMVTrend() {
        return this.orderGMVTrend;
    }

    public BigDecimal getCommissionTrend() {
        return this.commissionTrend;
    }

    public Long getTodayOrders() {
        return this.todayOrders;
    }

    public BigDecimal getTodayGMV() {
        return this.todayGMV;
    }

    public BigDecimal getTodaySettledCommission() {
        return this.todaySettledCommission;
    }

    public void setActiveInfluencers(Long activeInfluencers) {
        this.activeInfluencers = activeInfluencers;
    }

    public void setConversionOrders(Long conversionOrders) {
        this.conversionOrders = conversionOrders;
    }

    public void setOrderGMV(BigDecimal orderGMV) {
        this.orderGMV = orderGMV;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setActiveInfluencersTrend(BigDecimal activeInfluencersTrend) {
        this.activeInfluencersTrend = activeInfluencersTrend;
    }

    public void setConversionOrdersTrend(BigDecimal conversionOrdersTrend) {
        this.conversionOrdersTrend = conversionOrdersTrend;
    }

    public void setOrderGMVTrend(BigDecimal orderGMVTrend) {
        this.orderGMVTrend = orderGMVTrend;
    }

    public void setCommissionTrend(BigDecimal commissionTrend) {
        this.commissionTrend = commissionTrend;
    }

    public void setTodayOrders(Long todayOrders) {
        this.todayOrders = todayOrders;
    }

    public void setTodayGMV(BigDecimal todayGMV) {
        this.todayGMV = todayGMV;
    }

    public void setTodaySettledCommission(BigDecimal todaySettledCommission) {
        this.todaySettledCommission = todaySettledCommission;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DashboardStatsDto)) {
            return false;
        }
        DashboardStatsDto other = (DashboardStatsDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$activeInfluencers = this.getActiveInfluencers();
        Long other$activeInfluencers = other.getActiveInfluencers();
        if (this$activeInfluencers == null ? other$activeInfluencers != null : !((Object)this$activeInfluencers).equals(other$activeInfluencers)) {
            return false;
        }
        Long this$conversionOrders = this.getConversionOrders();
        Long other$conversionOrders = other.getConversionOrders();
        if (this$conversionOrders == null ? other$conversionOrders != null : !((Object)this$conversionOrders).equals(other$conversionOrders)) {
            return false;
        }
        Long this$todayOrders = this.getTodayOrders();
        Long other$todayOrders = other.getTodayOrders();
        if (this$todayOrders == null ? other$todayOrders != null : !((Object)this$todayOrders).equals(other$todayOrders)) {
            return false;
        }
        BigDecimal this$orderGMV = this.getOrderGMV();
        BigDecimal other$orderGMV = other.getOrderGMV();
        if (this$orderGMV == null ? other$orderGMV != null : !((Object)this$orderGMV).equals(other$orderGMV)) {
            return false;
        }
        BigDecimal this$commissionAmount = this.getCommissionAmount();
        BigDecimal other$commissionAmount = other.getCommissionAmount();
        if (this$commissionAmount == null ? other$commissionAmount != null : !((Object)this$commissionAmount).equals(other$commissionAmount)) {
            return false;
        }
        BigDecimal this$activeInfluencersTrend = this.getActiveInfluencersTrend();
        BigDecimal other$activeInfluencersTrend = other.getActiveInfluencersTrend();
        if (this$activeInfluencersTrend == null ? other$activeInfluencersTrend != null : !((Object)this$activeInfluencersTrend).equals(other$activeInfluencersTrend)) {
            return false;
        }
        BigDecimal this$conversionOrdersTrend = this.getConversionOrdersTrend();
        BigDecimal other$conversionOrdersTrend = other.getConversionOrdersTrend();
        if (this$conversionOrdersTrend == null ? other$conversionOrdersTrend != null : !((Object)this$conversionOrdersTrend).equals(other$conversionOrdersTrend)) {
            return false;
        }
        BigDecimal this$orderGMVTrend = this.getOrderGMVTrend();
        BigDecimal other$orderGMVTrend = other.getOrderGMVTrend();
        if (this$orderGMVTrend == null ? other$orderGMVTrend != null : !((Object)this$orderGMVTrend).equals(other$orderGMVTrend)) {
            return false;
        }
        BigDecimal this$commissionTrend = this.getCommissionTrend();
        BigDecimal other$commissionTrend = other.getCommissionTrend();
        if (this$commissionTrend == null ? other$commissionTrend != null : !((Object)this$commissionTrend).equals(other$commissionTrend)) {
            return false;
        }
        BigDecimal this$todayGMV = this.getTodayGMV();
        BigDecimal other$todayGMV = other.getTodayGMV();
        if (this$todayGMV == null ? other$todayGMV != null : !((Object)this$todayGMV).equals(other$todayGMV)) {
            return false;
        }
        BigDecimal this$todaySettledCommission = this.getTodaySettledCommission();
        BigDecimal other$todaySettledCommission = other.getTodaySettledCommission();
        return !(this$todaySettledCommission == null ? other$todaySettledCommission != null : !((Object)this$todaySettledCommission).equals(other$todaySettledCommission));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DashboardStatsDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $activeInfluencers = this.getActiveInfluencers();
        result = result * 59 + ($activeInfluencers == null ? 43 : ((Object)$activeInfluencers).hashCode());
        Long $conversionOrders = this.getConversionOrders();
        result = result * 59 + ($conversionOrders == null ? 43 : ((Object)$conversionOrders).hashCode());
        Long $todayOrders = this.getTodayOrders();
        result = result * 59 + ($todayOrders == null ? 43 : ((Object)$todayOrders).hashCode());
        BigDecimal $orderGMV = this.getOrderGMV();
        result = result * 59 + ($orderGMV == null ? 43 : ((Object)$orderGMV).hashCode());
        BigDecimal $commissionAmount = this.getCommissionAmount();
        result = result * 59 + ($commissionAmount == null ? 43 : ((Object)$commissionAmount).hashCode());
        BigDecimal $activeInfluencersTrend = this.getActiveInfluencersTrend();
        result = result * 59 + ($activeInfluencersTrend == null ? 43 : ((Object)$activeInfluencersTrend).hashCode());
        BigDecimal $conversionOrdersTrend = this.getConversionOrdersTrend();
        result = result * 59 + ($conversionOrdersTrend == null ? 43 : ((Object)$conversionOrdersTrend).hashCode());
        BigDecimal $orderGMVTrend = this.getOrderGMVTrend();
        result = result * 59 + ($orderGMVTrend == null ? 43 : ((Object)$orderGMVTrend).hashCode());
        BigDecimal $commissionTrend = this.getCommissionTrend();
        result = result * 59 + ($commissionTrend == null ? 43 : ((Object)$commissionTrend).hashCode());
        BigDecimal $todayGMV = this.getTodayGMV();
        result = result * 59 + ($todayGMV == null ? 43 : ((Object)$todayGMV).hashCode());
        BigDecimal $todaySettledCommission = this.getTodaySettledCommission();
        result = result * 59 + ($todaySettledCommission == null ? 43 : ((Object)$todaySettledCommission).hashCode());
        return result;
    }

    public String toString() {
        return "DashboardStatsDto(activeInfluencers=" + this.getActiveInfluencers() + ", conversionOrders=" + this.getConversionOrders() + ", orderGMV=" + this.getOrderGMV() + ", commissionAmount=" + this.getCommissionAmount() + ", activeInfluencersTrend=" + this.getActiveInfluencersTrend() + ", conversionOrdersTrend=" + this.getConversionOrdersTrend() + ", orderGMVTrend=" + this.getOrderGMVTrend() + ", commissionTrend=" + this.getCommissionTrend() + ", todayOrders=" + this.getTodayOrders() + ", todayGMV=" + this.getTodayGMV() + ", todaySettledCommission=" + this.getTodaySettledCommission() + ")";
    }
}

