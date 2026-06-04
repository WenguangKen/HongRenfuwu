package com.athlunakms.shopify.order.dto;

import com.athlunakms.shopify.order.dto.DuplicateCheckResult;
import java.time.LocalDateTime;

public class DuplicateCheckResult {
    private boolean hasDuplicate;
    private LocalDateTime lastOrderDate;
    private String lastOrderName;
    private Long lastOrderId;
    private String influencerName;

    public static DuplicateCheckResultBuilder builder() {
        return new DuplicateCheckResultBuilder();
    }

    public boolean isHasDuplicate() {
        return this.hasDuplicate;
    }

    public LocalDateTime getLastOrderDate() {
        return this.lastOrderDate;
    }

    public String getLastOrderName() {
        return this.lastOrderName;
    }

    public Long getLastOrderId() {
        return this.lastOrderId;
    }

    public String getInfluencerName() {
        return this.influencerName;
    }

    public void setHasDuplicate(boolean hasDuplicate) {
        this.hasDuplicate = hasDuplicate;
    }

    public void setLastOrderDate(LocalDateTime lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }

    public void setLastOrderName(String lastOrderName) {
        this.lastOrderName = lastOrderName;
    }

    public void setLastOrderId(Long lastOrderId) {
        this.lastOrderId = lastOrderId;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DuplicateCheckResult)) {
            return false;
        }
        DuplicateCheckResult other = (DuplicateCheckResult)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isHasDuplicate() != other.isHasDuplicate()) {
            return false;
        }
        Long this$lastOrderId = this.getLastOrderId();
        Long other$lastOrderId = other.getLastOrderId();
        if (this$lastOrderId == null ? other$lastOrderId != null : !((Object)this$lastOrderId).equals(other$lastOrderId)) {
            return false;
        }
        LocalDateTime this$lastOrderDate = this.getLastOrderDate();
        LocalDateTime other$lastOrderDate = other.getLastOrderDate();
        if (this$lastOrderDate == null ? other$lastOrderDate != null : !((Object)this$lastOrderDate).equals(other$lastOrderDate)) {
            return false;
        }
        String this$lastOrderName = this.getLastOrderName();
        String other$lastOrderName = other.getLastOrderName();
        if (this$lastOrderName == null ? other$lastOrderName != null : !this$lastOrderName.equals(other$lastOrderName)) {
            return false;
        }
        String this$influencerName = this.getInfluencerName();
        String other$influencerName = other.getInfluencerName();
        return !(this$influencerName == null ? other$influencerName != null : !this$influencerName.equals(other$influencerName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DuplicateCheckResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isHasDuplicate() ? 79 : 97);
        Long $lastOrderId = this.getLastOrderId();
        result = result * 59 + ($lastOrderId == null ? 43 : ((Object)$lastOrderId).hashCode());
        LocalDateTime $lastOrderDate = this.getLastOrderDate();
        result = result * 59 + ($lastOrderDate == null ? 43 : ((Object)$lastOrderDate).hashCode());
        String $lastOrderName = this.getLastOrderName();
        result = result * 59 + ($lastOrderName == null ? 43 : $lastOrderName.hashCode());
        String $influencerName = this.getInfluencerName();
        result = result * 59 + ($influencerName == null ? 43 : $influencerName.hashCode());
        return result;
    }

    public String toString() {
        return "DuplicateCheckResult(hasDuplicate=" + this.isHasDuplicate() + ", lastOrderDate=" + this.getLastOrderDate() + ", lastOrderName=" + this.getLastOrderName() + ", lastOrderId=" + this.getLastOrderId() + ", influencerName=" + this.getInfluencerName() + ")";
    }

    public DuplicateCheckResult() {
    }

    public DuplicateCheckResult(boolean hasDuplicate, LocalDateTime lastOrderDate, String lastOrderName, Long lastOrderId, String influencerName) {
        this.hasDuplicate = hasDuplicate;
        this.lastOrderDate = lastOrderDate;
        this.lastOrderName = lastOrderName;
        this.lastOrderId = lastOrderId;
        this.influencerName = influencerName;
    }

    public static class DuplicateCheckResultBuilder {
        private boolean hasDuplicate;
        private LocalDateTime lastOrderDate;
        private String lastOrderName;
        private Long lastOrderId;
        private String influencerName;

        DuplicateCheckResultBuilder() {
        }

        public DuplicateCheckResultBuilder hasDuplicate(boolean hasDuplicate) {
            this.hasDuplicate = hasDuplicate;
            return this;
        }

        public DuplicateCheckResultBuilder lastOrderDate(LocalDateTime lastOrderDate) {
            this.lastOrderDate = lastOrderDate;
            return this;
        }

        public DuplicateCheckResultBuilder lastOrderName(String lastOrderName) {
            this.lastOrderName = lastOrderName;
            return this;
        }

        public DuplicateCheckResultBuilder lastOrderId(Long lastOrderId) {
            this.lastOrderId = lastOrderId;
            return this;
        }

        public DuplicateCheckResultBuilder influencerName(String influencerName) {
            this.influencerName = influencerName;
            return this;
        }

        public DuplicateCheckResult build() {
            return new DuplicateCheckResult(hasDuplicate, lastOrderDate, lastOrderName, lastOrderId, influencerName);
        }

        public String toString() {
            return "DuplicateCheckResult.DuplicateCheckResultBuilder(hasDuplicate=" + this.hasDuplicate + ", lastOrderDate=" + this.lastOrderDate + ", lastOrderName=" + this.lastOrderName + ", lastOrderId=" + this.lastOrderId + ", influencerName=" + this.influencerName + ")";
        }
    }
}

