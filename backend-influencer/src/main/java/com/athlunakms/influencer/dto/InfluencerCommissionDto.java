package com.athlunakms.influencer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InfluencerCommissionDto {
    private Long influencerId;
    private String influencerName;
    private String influencerEmail;
    private BigDecimal pendingAmount;
    private BigDecimal paidAmount;
    private BigDecimal availableAmount;
    private BigDecimal totalAmount;
    private Long conversionOrderCount;
    private Long settledOrderCount;
    private Long sampleOrderCount;
    private Long payoutCount;
    private LocalDateTime lastDistributeTime;
    private LocalDateTime lastInitiatedTime;
    private LocalDateTime lastSettledTime;
    private String topDiscountCode;
    private List<String> discountCodes;

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public String getInfluencerEmail() {
        return this.influencerEmail;
    }

    public BigDecimal getPendingAmount() {
        return this.pendingAmount;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public BigDecimal getAvailableAmount() {
        return this.availableAmount;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Long getConversionOrderCount() {
        return this.conversionOrderCount;
    }

    public Long getSettledOrderCount() {
        return this.settledOrderCount;
    }

    public Long getSampleOrderCount() {
        return this.sampleOrderCount;
    }

    public Long getPayoutCount() {
        return this.payoutCount;
    }

    public LocalDateTime getLastDistributeTime() {
        return this.lastDistributeTime;
    }

    public LocalDateTime getLastInitiatedTime() {
        return this.lastInitiatedTime;
    }

    public LocalDateTime getLastSettledTime() {
        return this.lastSettledTime;
    }

    public String getTopDiscountCode() {
        return this.topDiscountCode;
    }

    public List<String> getDiscountCodes() {
        return this.discountCodes;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public void setInfluencerEmail(String influencerEmail) {
        this.influencerEmail = influencerEmail;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setConversionOrderCount(Long conversionOrderCount) {
        this.conversionOrderCount = conversionOrderCount;
    }

    public void setSettledOrderCount(Long settledOrderCount) {
        this.settledOrderCount = settledOrderCount;
    }

    public void setSampleOrderCount(Long sampleOrderCount) {
        this.sampleOrderCount = sampleOrderCount;
    }

    public void setPayoutCount(Long payoutCount) {
        this.payoutCount = payoutCount;
    }

    public void setLastDistributeTime(LocalDateTime lastDistributeTime) {
        this.lastDistributeTime = lastDistributeTime;
    }

    public void setLastInitiatedTime(LocalDateTime lastInitiatedTime) {
        this.lastInitiatedTime = lastInitiatedTime;
    }

    public void setLastSettledTime(LocalDateTime lastSettledTime) {
        this.lastSettledTime = lastSettledTime;
    }

    public void setTopDiscountCode(String topDiscountCode) {
        this.topDiscountCode = topDiscountCode;
    }

    public void setDiscountCodes(List<String> discountCodes) {
        this.discountCodes = discountCodes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerCommissionDto)) {
            return false;
        }
        InfluencerCommissionDto other = (InfluencerCommissionDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Long this$conversionOrderCount = this.getConversionOrderCount();
        Long other$conversionOrderCount = other.getConversionOrderCount();
        if (this$conversionOrderCount == null ? other$conversionOrderCount != null : !((Object)this$conversionOrderCount).equals(other$conversionOrderCount)) {
            return false;
        }
        Long this$settledOrderCount = this.getSettledOrderCount();
        Long other$settledOrderCount = other.getSettledOrderCount();
        if (this$settledOrderCount == null ? other$settledOrderCount != null : !((Object)this$settledOrderCount).equals(other$settledOrderCount)) {
            return false;
        }
        Long this$sampleOrderCount = this.getSampleOrderCount();
        Long other$sampleOrderCount = other.getSampleOrderCount();
        if (this$sampleOrderCount == null ? other$sampleOrderCount != null : !((Object)this$sampleOrderCount).equals(other$sampleOrderCount)) {
            return false;
        }
        Long this$payoutCount = this.getPayoutCount();
        Long other$payoutCount = other.getPayoutCount();
        if (this$payoutCount == null ? other$payoutCount != null : !((Object)this$payoutCount).equals(other$payoutCount)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        if (this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName)) {
            return false;
        }
        String this$influencerEmail = this.getInfluencerEmail();
        String other$influencerEmail = other.getInfluencerEmail();
        if (this$influencerEmail == null ? other$influencerEmail != null : !this$influencerEmail.equals(other$influencerEmail)) {
            return false;
        }
        BigDecimal this$pendingAmount = this.getPendingAmount();
        BigDecimal other$pendingAmount = other.getPendingAmount();
        if (this$pendingAmount == null ? other$pendingAmount != null : !((Object)this$pendingAmount).equals(other$pendingAmount)) {
            return false;
        }
        BigDecimal this$paidAmount = this.getPaidAmount();
        BigDecimal other$paidAmount = other.getPaidAmount();
        if (this$paidAmount == null ? other$paidAmount != null : !((Object)this$paidAmount).equals(other$paidAmount)) {
            return false;
        }
        BigDecimal this$availableAmount = this.getAvailableAmount();
        BigDecimal other$availableAmount = other.getAvailableAmount();
        if (this$availableAmount == null ? other$availableAmount != null : !((Object)this$availableAmount).equals(other$availableAmount)) {
            return false;
        }
        BigDecimal this$totalAmount = this.getTotalAmount();
        BigDecimal other$totalAmount = other.getTotalAmount();
        if (this$totalAmount == null ? other$totalAmount != null : !((Object)this$totalAmount).equals(other$totalAmount)) {
            return false;
        }
        LocalDateTime this$lastDistributeTime = this.getLastDistributeTime();
        LocalDateTime other$lastDistributeTime = other.getLastDistributeTime();
        if (this$lastDistributeTime == null ? other$lastDistributeTime != null : !((Object)this$lastDistributeTime).equals(other$lastDistributeTime)) {
            return false;
        }
        LocalDateTime this$lastInitiatedTime = this.getLastInitiatedTime();
        LocalDateTime other$lastInitiatedTime = other.getLastInitiatedTime();
        if (this$lastInitiatedTime == null ? other$lastInitiatedTime != null : !((Object)this$lastInitiatedTime).equals(other$lastInitiatedTime)) {
            return false;
        }
        LocalDateTime this$lastSettledTime = this.getLastSettledTime();
        LocalDateTime other$lastSettledTime = other.getLastSettledTime();
        if (this$lastSettledTime == null ? other$lastSettledTime != null : !((Object)this$lastSettledTime).equals(other$lastSettledTime)) {
            return false;
        }
        String this$topDiscountCode = this.getTopDiscountCode();
        String other$topDiscountCode = other.getTopDiscountCode();
        if (this$topDiscountCode == null ? other$topDiscountCode != null : !this$topDiscountCode.equals(other$topDiscountCode)) {
            return false;
        }
        List this$discountCodes = this.getDiscountCodes();
        List other$discountCodes = other.getDiscountCodes();
        return !(this$discountCodes == null ? other$discountCodes != null : !((Object)this$discountCodes).equals(other$discountCodes));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerCommissionDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $conversionOrderCount = this.getConversionOrderCount();
        result = result * 59 + ($conversionOrderCount == null ? 43 : ((Object)$conversionOrderCount).hashCode());
        Long $settledOrderCount = this.getSettledOrderCount();
        result = result * 59 + ($settledOrderCount == null ? 43 : ((Object)$settledOrderCount).hashCode());
        Long $sampleOrderCount = this.getSampleOrderCount();
        result = result * 59 + ($sampleOrderCount == null ? 43 : ((Object)$sampleOrderCount).hashCode());
        Long $payoutCount = this.getPayoutCount();
        result = result * 59 + ($payoutCount == null ? 43 : ((Object)$payoutCount).hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        String $influencerEmail = this.getInfluencerEmail();
        result = result * 59 + ($influencerEmail == null ? 43 : $influencerEmail.hashCode());
        BigDecimal $pendingAmount = this.getPendingAmount();
        result = result * 59 + ($pendingAmount == null ? 43 : ((Object)$pendingAmount).hashCode());
        BigDecimal $paidAmount = this.getPaidAmount();
        result = result * 59 + ($paidAmount == null ? 43 : ((Object)$paidAmount).hashCode());
        BigDecimal $availableAmount = this.getAvailableAmount();
        result = result * 59 + ($availableAmount == null ? 43 : ((Object)$availableAmount).hashCode());
        BigDecimal $totalAmount = this.getTotalAmount();
        result = result * 59 + ($totalAmount == null ? 43 : ((Object)$totalAmount).hashCode());
        LocalDateTime $lastDistributeTime = this.getLastDistributeTime();
        result = result * 59 + ($lastDistributeTime == null ? 43 : ((Object)$lastDistributeTime).hashCode());
        LocalDateTime $lastInitiatedTime = this.getLastInitiatedTime();
        result = result * 59 + ($lastInitiatedTime == null ? 43 : ((Object)$lastInitiatedTime).hashCode());
        LocalDateTime $lastSettledTime = this.getLastSettledTime();
        result = result * 59 + ($lastSettledTime == null ? 43 : ((Object)$lastSettledTime).hashCode());
        String $topDiscountCode = this.getTopDiscountCode();
        result = result * 59 + ($topDiscountCode == null ? 43 : $topDiscountCode.hashCode());
        List $discountCodes = this.getDiscountCodes();
        result = result * 59 + ($discountCodes == null ? 43 : ((Object)$discountCodes).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerCommissionDto(influencerId=" + this.getInfluencerId() + ", influencerName=" + this.getInfluencerName() + ", influencerEmail=" + this.getInfluencerEmail() + ", pendingAmount=" + this.getPendingAmount() + ", paidAmount=" + this.getPaidAmount() + ", availableAmount=" + this.getAvailableAmount() + ", totalAmount=" + this.getTotalAmount() + ", conversionOrderCount=" + this.getConversionOrderCount() + ", settledOrderCount=" + this.getSettledOrderCount() + ", sampleOrderCount=" + this.getSampleOrderCount() + ", payoutCount=" + this.getPayoutCount() + ", lastDistributeTime=" + this.getLastDistributeTime() + ", lastInitiatedTime=" + this.getLastInitiatedTime() + ", lastSettledTime=" + this.getLastSettledTime() + ", topDiscountCode=" + this.getTopDiscountCode() + ", discountCodes=" + this.getDiscountCodes() + ")";
    }
}

