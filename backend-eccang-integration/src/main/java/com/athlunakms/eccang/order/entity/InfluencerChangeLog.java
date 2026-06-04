package com.athlunakms.eccang.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="influencer_change_log")
public class InfluencerChangeLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="influencer_id", nullable=false)
    private Long influencerId;
    @Column(name="field_name", nullable=false)
    private String fieldName;
    @Column(name="old_value")
    private String oldValue;
    @Column(name="new_value")
    private String newValue;
    @Column(name="operator")
    private String operator;
    @Column(name="operator_id")
    private Long operatorId;
    @Column(name="remark")
    private String remark;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public String getOperator() {
        return this.operator;
    }

    public Long getOperatorId() {
        return this.operatorId;
    }

    public String getRemark() {
        return this.remark;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerChangeLog)) {
            return false;
        }
        InfluencerChangeLog other = (InfluencerChangeLog)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$influencerId = this.getInfluencerId();
        Long other$influencerId = other.getInfluencerId();
        if (this$influencerId == null ? other$influencerId != null : !((Object)this$influencerId).equals(other$influencerId)) {
            return false;
        }
        Long this$operatorId = this.getOperatorId();
        Long other$operatorId = other.getOperatorId();
        if (this$operatorId == null ? other$operatorId != null : !((Object)this$operatorId).equals(other$operatorId)) {
            return false;
        }
        String this$fieldName = this.getFieldName();
        String other$fieldName = other.getFieldName();
        if (this$fieldName == null ? other$fieldName != null : !this$fieldName.equals(other$fieldName)) {
            return false;
        }
        String this$oldValue = this.getOldValue();
        String other$oldValue = other.getOldValue();
        if (this$oldValue == null ? other$oldValue != null : !this$oldValue.equals(other$oldValue)) {
            return false;
        }
        String this$newValue = this.getNewValue();
        String other$newValue = other.getNewValue();
        if (this$newValue == null ? other$newValue != null : !this$newValue.equals(other$newValue)) {
            return false;
        }
        String this$operator = this.getOperator();
        String other$operator = other.getOperator();
        if (this$operator == null ? other$operator != null : !this$operator.equals(other$operator)) {
            return false;
        }
        String this$remark = this.getRemark();
        String other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerChangeLog;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        Long $operatorId = this.getOperatorId();
        result = result * 59 + ($operatorId == null ? 43 : ((Object)$operatorId).hashCode());
        String $fieldName = this.getFieldName();
        result = result * 59 + ($fieldName == null ? 43 : $fieldName.hashCode());
        String $oldValue = this.getOldValue();
        result = result * 59 + ($oldValue == null ? 43 : $oldValue.hashCode());
        String $newValue = this.getNewValue();
        result = result * 59 + ($newValue == null ? 43 : $newValue.hashCode());
        String $operator = this.getOperator();
        result = result * 59 + ($operator == null ? 43 : $operator.hashCode());
        String $remark = this.getRemark();
        result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerChangeLog(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", fieldName=" + this.getFieldName() + ", oldValue=" + this.getOldValue() + ", newValue=" + this.getNewValue() + ", operator=" + this.getOperator() + ", operatorId=" + this.getOperatorId() + ", remark=" + this.getRemark() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

