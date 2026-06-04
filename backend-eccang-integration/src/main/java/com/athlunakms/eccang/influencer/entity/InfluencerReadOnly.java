package com.athlunakms.eccang.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="influencer")
public class InfluencerReadOnly {
    @Id
    private Long id;
    @Column(name="real_name")
    private String realName;
    @Column(name="nick_name")
    private String nickName;
    @Column(name="influencer_discount_code")
    private String influencerDiscountCode;
    @Column(name="commission_rate", precision=5, scale=2)
    private BigDecimal commissionRate;
    @Column(name="owner_id")
    private Long ownerId;
    @Column(name="contact_person_id")
    private Long contactPersonId;
    @Column(name="tags", columnDefinition="TEXT")
    private String tags;
    @Column(name="default_handle")
    private String defaultHandle;

    public Long getId() {
        return this.id;
    }

    public String getRealName() {
        return this.realName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getInfluencerDiscountCode() {
        return this.influencerDiscountCode;
    }

    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
    }

    public String getTags() {
        return this.tags;
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

    public void setInfluencerDiscountCode(String influencerDiscountCode) {
        this.influencerDiscountCode = influencerDiscountCode;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDefaultHandle() {
        return this.defaultHandle;
    }

    public void setDefaultHandle(String defaultHandle) {
        this.defaultHandle = defaultHandle;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerReadOnly)) {
            return false;
        }
        InfluencerReadOnly other = (InfluencerReadOnly)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$ownerId = this.getOwnerId();
        Long other$ownerId = other.getOwnerId();
        if (this$ownerId == null ? other$ownerId != null : !((Object)this$ownerId).equals(other$ownerId)) {
            return false;
        }
        Long this$contactPersonId = this.getContactPersonId();
        Long other$contactPersonId = other.getContactPersonId();
        if (this$contactPersonId == null ? other$contactPersonId != null : !((Object)this$contactPersonId).equals(other$contactPersonId)) {
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
        String this$influencerDiscountCode = this.getInfluencerDiscountCode();
        String other$influencerDiscountCode = other.getInfluencerDiscountCode();
        if (this$influencerDiscountCode == null ? other$influencerDiscountCode != null : !this$influencerDiscountCode.equals(other$influencerDiscountCode)) {
            return false;
        }
        BigDecimal this$commissionRate = this.getCommissionRate();
        BigDecimal other$commissionRate = other.getCommissionRate();
        if (this$commissionRate == null ? other$commissionRate != null : !((Object)this$commissionRate).equals(other$commissionRate)) {
            return false;
        }
        String this$tags = this.getTags();
        String other$tags = other.getTags();
        return !(this$tags == null ? other$tags != null : !this$tags.equals(other$tags));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerReadOnly;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $ownerId = this.getOwnerId();
        result = result * 59 + ($ownerId == null ? 43 : ((Object)$ownerId).hashCode());
        Long $contactPersonId = this.getContactPersonId();
        result = result * 59 + ($contactPersonId == null ? 43 : ((Object)$contactPersonId).hashCode());
        String $realName = this.getRealName();
        result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
        String $nickName = this.getNickName();
        result = result * 59 + ($nickName == null ? 43 : $nickName.hashCode());
        String $influencerDiscountCode = this.getInfluencerDiscountCode();
        result = result * 59 + ($influencerDiscountCode == null ? 43 : $influencerDiscountCode.hashCode());
        BigDecimal $commissionRate = this.getCommissionRate();
        result = result * 59 + ($commissionRate == null ? 43 : ((Object)$commissionRate).hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerReadOnly(id=" + this.getId() + ", realName=" + this.getRealName() + ", nickName=" + this.getNickName() + ", influencerDiscountCode=" + this.getInfluencerDiscountCode() + ", commissionRate=" + this.getCommissionRate() + ", ownerId=" + this.getOwnerId() + ", contactPersonId=" + this.getContactPersonId() + ", tags=" + this.getTags() + ")";
    }
}

