package com.athlunakms.influencer.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long activeInfluencers;
    private long conversionOrders;
    private BigDecimal orderGMV;
    private BigDecimal commissionAmount;
    private double activeInfluencersTrend;
    private double conversionOrdersTrend;
    private double orderGMVTrend;
    private double commissionTrend;
    private long todayOrders;
    private BigDecimal todayGMV;
    private BigDecimal todaySettledCommission;
    private long previousActiveInfluencers;
    private long previousConversionOrders;
    private BigDecimal previousOrderGMV;
    private BigDecimal previousCommissionAmount;
    private String currentMonthLabel;
    private String previousMonthLabel;
    private boolean dataInsufficient;

    public long getActiveInfluencers() {
        return this.activeInfluencers;
    }

    public long getConversionOrders() {
        return this.conversionOrders;
    }

    public BigDecimal getOrderGMV() {
        return this.orderGMV;
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    public double getActiveInfluencersTrend() {
        return this.activeInfluencersTrend;
    }

    public double getConversionOrdersTrend() {
        return this.conversionOrdersTrend;
    }

    public double getOrderGMVTrend() {
        return this.orderGMVTrend;
    }

    public double getCommissionTrend() {
        return this.commissionTrend;
    }

    public long getTodayOrders() {
        return this.todayOrders;
    }

    public BigDecimal getTodayGMV() {
        return this.todayGMV;
    }

    public BigDecimal getTodaySettledCommission() {
        return this.todaySettledCommission;
    }

    public long getPreviousActiveInfluencers() {
        return this.previousActiveInfluencers;
    }

    public long getPreviousConversionOrders() {
        return this.previousConversionOrders;
    }

    public BigDecimal getPreviousOrderGMV() {
        return this.previousOrderGMV;
    }

    public BigDecimal getPreviousCommissionAmount() {
        return this.previousCommissionAmount;
    }

    public String getCurrentMonthLabel() {
        return this.currentMonthLabel;
    }

    public String getPreviousMonthLabel() {
        return this.previousMonthLabel;
    }

    public boolean isDataInsufficient() {
        return this.dataInsufficient;
    }

    public void setActiveInfluencers(long activeInfluencers) {
        this.activeInfluencers = activeInfluencers;
    }

    public void setConversionOrders(long conversionOrders) {
        this.conversionOrders = conversionOrders;
    }

    public void setOrderGMV(BigDecimal orderGMV) {
        this.orderGMV = orderGMV;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setActiveInfluencersTrend(double activeInfluencersTrend) {
        this.activeInfluencersTrend = activeInfluencersTrend;
    }

    public void setConversionOrdersTrend(double conversionOrdersTrend) {
        this.conversionOrdersTrend = conversionOrdersTrend;
    }

    public void setOrderGMVTrend(double orderGMVTrend) {
        this.orderGMVTrend = orderGMVTrend;
    }

    public void setCommissionTrend(double commissionTrend) {
        this.commissionTrend = commissionTrend;
    }

    public void setTodayOrders(long todayOrders) {
        this.todayOrders = todayOrders;
    }

    public void setTodayGMV(BigDecimal todayGMV) {
        this.todayGMV = todayGMV;
    }

    public void setTodaySettledCommission(BigDecimal todaySettledCommission) {
        this.todaySettledCommission = todaySettledCommission;
    }

    public void setPreviousActiveInfluencers(long previousActiveInfluencers) {
        this.previousActiveInfluencers = previousActiveInfluencers;
    }

    public void setPreviousConversionOrders(long previousConversionOrders) {
        this.previousConversionOrders = previousConversionOrders;
    }

    public void setPreviousOrderGMV(BigDecimal previousOrderGMV) {
        this.previousOrderGMV = previousOrderGMV;
    }

    public void setPreviousCommissionAmount(BigDecimal previousCommissionAmount) {
        this.previousCommissionAmount = previousCommissionAmount;
    }

    public void setCurrentMonthLabel(String currentMonthLabel) {
        this.currentMonthLabel = currentMonthLabel;
    }

    public void setPreviousMonthLabel(String previousMonthLabel) {
        this.previousMonthLabel = previousMonthLabel;
    }

    public void setDataInsufficient(boolean dataInsufficient) {
        this.dataInsufficient = dataInsufficient;
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
        if (this.getActiveInfluencers() != other.getActiveInfluencers()) {
            return false;
        }
        if (this.getConversionOrders() != other.getConversionOrders()) {
            return false;
        }
        if (Double.compare(this.getActiveInfluencersTrend(), other.getActiveInfluencersTrend()) != 0) {
            return false;
        }
        if (Double.compare(this.getConversionOrdersTrend(), other.getConversionOrdersTrend()) != 0) {
            return false;
        }
        if (Double.compare(this.getOrderGMVTrend(), other.getOrderGMVTrend()) != 0) {
            return false;
        }
        if (Double.compare(this.getCommissionTrend(), other.getCommissionTrend()) != 0) {
            return false;
        }
        if (this.getTodayOrders() != other.getTodayOrders()) {
            return false;
        }
        if (this.getPreviousActiveInfluencers() != other.getPreviousActiveInfluencers()) {
            return false;
        }
        if (this.getPreviousConversionOrders() != other.getPreviousConversionOrders()) {
            return false;
        }
        if (this.isDataInsufficient() != other.isDataInsufficient()) {
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
        BigDecimal this$todayGMV = this.getTodayGMV();
        BigDecimal other$todayGMV = other.getTodayGMV();
        if (this$todayGMV == null ? other$todayGMV != null : !((Object)this$todayGMV).equals(other$todayGMV)) {
            return false;
        }
        BigDecimal this$todaySettledCommission = this.getTodaySettledCommission();
        BigDecimal other$todaySettledCommission = other.getTodaySettledCommission();
        if (this$todaySettledCommission == null ? other$todaySettledCommission != null : !((Object)this$todaySettledCommission).equals(other$todaySettledCommission)) {
            return false;
        }
        BigDecimal this$previousOrderGMV = this.getPreviousOrderGMV();
        BigDecimal other$previousOrderGMV = other.getPreviousOrderGMV();
        if (this$previousOrderGMV == null ? other$previousOrderGMV != null : !((Object)this$previousOrderGMV).equals(other$previousOrderGMV)) {
            return false;
        }
        BigDecimal this$previousCommissionAmount = this.getPreviousCommissionAmount();
        BigDecimal other$previousCommissionAmount = other.getPreviousCommissionAmount();
        if (this$previousCommissionAmount == null ? other$previousCommissionAmount != null : !((Object)this$previousCommissionAmount).equals(other$previousCommissionAmount)) {
            return false;
        }
        String this$currentMonthLabel = this.getCurrentMonthLabel();
        String other$currentMonthLabel = other.getCurrentMonthLabel();
        if (this$currentMonthLabel == null ? other$currentMonthLabel != null : !this$currentMonthLabel.equals(other$currentMonthLabel)) {
            return false;
        }
        String this$previousMonthLabel = this.getPreviousMonthLabel();
        String other$previousMonthLabel = other.getPreviousMonthLabel();
        return !(this$previousMonthLabel == null ? other$previousMonthLabel != null : !this$previousMonthLabel.equals(other$previousMonthLabel));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DashboardStatsDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $activeInfluencers = this.getActiveInfluencers();
        result = result * 59 + (int)($activeInfluencers >>> 32 ^ $activeInfluencers);
        long $conversionOrders = this.getConversionOrders();
        result = result * 59 + (int)($conversionOrders >>> 32 ^ $conversionOrders);
        long $activeInfluencersTrend = Double.doubleToLongBits(this.getActiveInfluencersTrend());
        result = result * 59 + (int)($activeInfluencersTrend >>> 32 ^ $activeInfluencersTrend);
        long $conversionOrdersTrend = Double.doubleToLongBits(this.getConversionOrdersTrend());
        result = result * 59 + (int)($conversionOrdersTrend >>> 32 ^ $conversionOrdersTrend);
        long $orderGMVTrend = Double.doubleToLongBits(this.getOrderGMVTrend());
        result = result * 59 + (int)($orderGMVTrend >>> 32 ^ $orderGMVTrend);
        long $commissionTrend = Double.doubleToLongBits(this.getCommissionTrend());
        result = result * 59 + (int)($commissionTrend >>> 32 ^ $commissionTrend);
        long $todayOrders = this.getTodayOrders();
        result = result * 59 + (int)($todayOrders >>> 32 ^ $todayOrders);
        long $previousActiveInfluencers = this.getPreviousActiveInfluencers();
        result = result * 59 + (int)($previousActiveInfluencers >>> 32 ^ $previousActiveInfluencers);
        long $previousConversionOrders = this.getPreviousConversionOrders();
        result = result * 59 + (int)($previousConversionOrders >>> 32 ^ $previousConversionOrders);
        result = result * 59 + (this.isDataInsufficient() ? 79 : 97);
        BigDecimal $orderGMV = this.getOrderGMV();
        result = result * 59 + ($orderGMV == null ? 43 : ((Object)$orderGMV).hashCode());
        BigDecimal $commissionAmount = this.getCommissionAmount();
        result = result * 59 + ($commissionAmount == null ? 43 : ((Object)$commissionAmount).hashCode());
        BigDecimal $todayGMV = this.getTodayGMV();
        result = result * 59 + ($todayGMV == null ? 43 : ((Object)$todayGMV).hashCode());
        BigDecimal $todaySettledCommission = this.getTodaySettledCommission();
        result = result * 59 + ($todaySettledCommission == null ? 43 : ((Object)$todaySettledCommission).hashCode());
        BigDecimal $previousOrderGMV = this.getPreviousOrderGMV();
        result = result * 59 + ($previousOrderGMV == null ? 43 : ((Object)$previousOrderGMV).hashCode());
        BigDecimal $previousCommissionAmount = this.getPreviousCommissionAmount();
        result = result * 59 + ($previousCommissionAmount == null ? 43 : ((Object)$previousCommissionAmount).hashCode());
        String $currentMonthLabel = this.getCurrentMonthLabel();
        result = result * 59 + ($currentMonthLabel == null ? 43 : $currentMonthLabel.hashCode());
        String $previousMonthLabel = this.getPreviousMonthLabel();
        result = result * 59 + ($previousMonthLabel == null ? 43 : $previousMonthLabel.hashCode());
        return result;
    }

    public String toString() {
        return "DashboardStatsDto(activeInfluencers=" + this.getActiveInfluencers() + ", conversionOrders=" + this.getConversionOrders() + ", orderGMV=" + this.getOrderGMV() + ", commissionAmount=" + this.getCommissionAmount() + ", activeInfluencersTrend=" + this.getActiveInfluencersTrend() + ", conversionOrdersTrend=" + this.getConversionOrdersTrend() + ", orderGMVTrend=" + this.getOrderGMVTrend() + ", commissionTrend=" + this.getCommissionTrend() + ", todayOrders=" + this.getTodayOrders() + ", todayGMV=" + this.getTodayGMV() + ", todaySettledCommission=" + this.getTodaySettledCommission() + ", previousActiveInfluencers=" + this.getPreviousActiveInfluencers() + ", previousConversionOrders=" + this.getPreviousConversionOrders() + ", previousOrderGMV=" + this.getPreviousOrderGMV() + ", previousCommissionAmount=" + this.getPreviousCommissionAmount() + ", currentMonthLabel=" + this.getCurrentMonthLabel() + ", previousMonthLabel=" + this.getPreviousMonthLabel() + ", dataInsufficient=" + this.isDataInsufficient() + ")";
    }
}

