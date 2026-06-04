package com.athlunakms.user.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardTrendDto {
    private List<String> dates;
    private List<Long> orderCounts;
    private List<BigDecimal> gmvData;
    private List<BigDecimal> commissionData;

    public List<String> getDates() {
        return this.dates;
    }

    public List<Long> getOrderCounts() {
        return this.orderCounts;
    }

    public List<BigDecimal> getGmvData() {
        return this.gmvData;
    }

    public List<BigDecimal> getCommissionData() {
        return this.commissionData;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void setOrderCounts(List<Long> orderCounts) {
        this.orderCounts = orderCounts;
    }

    public void setGmvData(List<BigDecimal> gmvData) {
        this.gmvData = gmvData;
    }

    public void setCommissionData(List<BigDecimal> commissionData) {
        this.commissionData = commissionData;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DashboardTrendDto)) {
            return false;
        }
        DashboardTrendDto other = (DashboardTrendDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$dates = this.getDates();
        List other$dates = other.getDates();
        if (this$dates == null ? other$dates != null : !((Object)this$dates).equals(other$dates)) {
            return false;
        }
        List this$orderCounts = this.getOrderCounts();
        List other$orderCounts = other.getOrderCounts();
        if (this$orderCounts == null ? other$orderCounts != null : !((Object)this$orderCounts).equals(other$orderCounts)) {
            return false;
        }
        List this$gmvData = this.getGmvData();
        List other$gmvData = other.getGmvData();
        if (this$gmvData == null ? other$gmvData != null : !((Object)this$gmvData).equals(other$gmvData)) {
            return false;
        }
        List this$commissionData = this.getCommissionData();
        List other$commissionData = other.getCommissionData();
        return !(this$commissionData == null ? other$commissionData != null : !((Object)this$commissionData).equals(other$commissionData));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DashboardTrendDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $dates = this.getDates();
        result = result * 59 + ($dates == null ? 43 : ((Object)$dates).hashCode());
        List $orderCounts = this.getOrderCounts();
        result = result * 59 + ($orderCounts == null ? 43 : ((Object)$orderCounts).hashCode());
        List $gmvData = this.getGmvData();
        result = result * 59 + ($gmvData == null ? 43 : ((Object)$gmvData).hashCode());
        List $commissionData = this.getCommissionData();
        result = result * 59 + ($commissionData == null ? 43 : ((Object)$commissionData).hashCode());
        return result;
    }

    public String toString() {
        return "DashboardTrendDto(dates=" + this.getDates() + ", orderCounts=" + this.getOrderCounts() + ", gmvData=" + this.getGmvData() + ", commissionData=" + this.getCommissionData() + ")";
    }
}

