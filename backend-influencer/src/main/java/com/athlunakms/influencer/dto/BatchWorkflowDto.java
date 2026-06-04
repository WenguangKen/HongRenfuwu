package com.athlunakms.influencer.dto;

import com.athlunakms.influencer.entity.Influencer;
import java.util.List;

public class BatchWorkflowDto {
    private List<Long> ids;
    private Influencer.Stage targetStage;
    private Influencer.Status targetStatus;
    private String reason;

    public List<Long> getIds() {
        return this.ids;
    }

    public Influencer.Stage getTargetStage() {
        return this.targetStage;
    }

    public Influencer.Status getTargetStatus() {
        return this.targetStatus;
    }

    public String getReason() {
        return this.reason;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public void setTargetStage(Influencer.Stage targetStage) {
        this.targetStage = targetStage;
    }

    public void setTargetStatus(Influencer.Status targetStatus) {
        this.targetStatus = targetStatus;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BatchWorkflowDto)) {
            return false;
        }
        BatchWorkflowDto other = (BatchWorkflowDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$ids = this.getIds();
        List other$ids = other.getIds();
        if (this$ids == null ? other$ids != null : !((Object)this$ids).equals(other$ids)) {
            return false;
        }
        Influencer.Stage this$targetStage = this.getTargetStage();
        Influencer.Stage other$targetStage = other.getTargetStage();
        if (this$targetStage == null ? other$targetStage != null : !this$targetStage.equals(other$targetStage)) {
            return false;
        }
        Influencer.Status this$targetStatus = this.getTargetStatus();
        Influencer.Status other$targetStatus = other.getTargetStatus();
        if (this$targetStatus == null ? other$targetStatus != null : !this$targetStatus.equals(other$targetStatus)) {
            return false;
        }
        String this$reason = this.getReason();
        String other$reason = other.getReason();
        return !(this$reason == null ? other$reason != null : !this$reason.equals(other$reason));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BatchWorkflowDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : ((Object)$ids).hashCode());
        Influencer.Stage $targetStage = this.getTargetStage();
        result = result * 59 + ($targetStage == null ? 43 : $targetStage.hashCode());
        Influencer.Status $targetStatus = this.getTargetStatus();
        result = result * 59 + ($targetStatus == null ? 43 : $targetStatus.hashCode());
        String $reason = this.getReason();
        result = result * 59 + ($reason == null ? 43 : $reason.hashCode());
        return result;
    }

    public String toString() {
        return "BatchWorkflowDto(ids=" + this.getIds() + ", targetStage=" + this.getTargetStage() + ", targetStatus=" + this.getTargetStatus() + ", reason=" + this.getReason() + ")";
    }
}

