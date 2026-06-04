package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "influencer")
public class InfluencerReadOnly {
    @Id
    private Long id;
    @Column(name = "real_name")
    private String realName;
    @Column(name = "nick_name")
    private String nickName;
    @Column(name = "influencer_type")
    private String influencerType;
    @Column(name = "status")
    private String influencerStatus;

    public Long getId() {
        return this.id;
    }

    public String getRealName() {
        return this.realName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getInfluencerType() {
        return this.influencerType;
    }

    public String getInfluencerStatus() {
        return this.influencerStatus;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setInfluencerType(String influencerType) {
        this.influencerType = influencerType;
    }

    public void setInfluencerStatus(String influencerStatus) {
        this.influencerStatus = influencerStatus;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerReadOnly)) {
            return false;
        }
        InfluencerReadOnly other = (InfluencerReadOnly) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object) this$id).equals(other$id)) {
            return false;
        }
        String this$realName = this.getRealName();
        String other$realName = other.getRealName();
        if (this$realName == null ? other$realName != null : !this$realName.equals(other$realName)) {
            return false;
        }
        String this$nickName = this.getNickName();
        String other$nickName = other.getNickName();
        if (this$nickName == null ? other$nickName != null : !this$nickName.equals(other$nickName)) {
            return false;
        }
        String this$influencerType = this.getInfluencerType();
        String other$influencerType = other.getInfluencerType();
        if (this$influencerType == null ? other$influencerType != null
                : !this$influencerType.equals(other$influencerType)) {
            return false;
        }
        String this$influencerStatus = this.getInfluencerStatus();
        String other$influencerStatus = other.getInfluencerStatus();
        return !(this$influencerStatus == null ? other$influencerStatus != null
                : !this$influencerStatus.equals(other$influencerStatus));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerReadOnly;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object) $id).hashCode());
        String $realName = this.getRealName();
        result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
        String $nickName = this.getNickName();
        result = result * 59 + ($nickName == null ? 43 : $nickName.hashCode());
        String $influencerType = this.getInfluencerType();
        result = result * 59 + ($influencerType == null ? 43 : $influencerType.hashCode());
        String $influencerStatus = this.getInfluencerStatus();
        result = result * 59 + ($influencerStatus == null ? 43 : $influencerStatus.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerReadOnly(id=" + this.getId() + ", realName=" + this.getRealName() + ", nickName="
                + this.getNickName() + ", influencerType=" + this.getInfluencerType() + ", influencerStatus="
                + this.getInfluencerStatus() + ")";
    }
}
