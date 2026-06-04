package com.athlunakms.influencer.dto;

import java.math.BigDecimal;
import java.util.List;

public class InfluencerUpdateDto {
    private Long id;
    private Integer version;
    private String realName;
    private String nickName;
    private String avatarUrl;
    private String email;
    private String phone;
    private String country;
    private String language;
    private String race;
    private Integer gender;
    private String description;
    private String source;
    private String influencerType;
    private Boolean isPaid;
    private String brand;
    private List<Long> tagIds;
    private BigDecimal commissionRate;
    private String paymentMethod;
    private String paymentAccount;
    private Long defaultSocialId;
    private Long ownerId;
    private Long contactPersonId;
    private String auxiliaryEmails;

    public Long getId() {
        return this.id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public String getRealName() {
        return this.realName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getCountry() {
        return this.country;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getRace() {
        return this.race;
    }

    public Integer getGender() {
        return this.gender;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSource() {
        return this.source;
    }

    public String getInfluencerType() {
        return this.influencerType;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public String getBrand() {
        return this.brand;
    }

    public List<Long> getTagIds() {
        return this.tagIds;
    }

    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getPaymentAccount() {
        return this.paymentAccount;
    }

    public Long getDefaultSocialId() {
        return this.defaultSocialId;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
    }

    public String getAuxiliaryEmails() {
        return this.auxiliaryEmails;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setInfluencerType(String influencerType) {
        this.influencerType = influencerType;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public void setDefaultSocialId(Long defaultSocialId) {
        this.defaultSocialId = defaultSocialId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setAuxiliaryEmails(String auxiliaryEmails) {
        this.auxiliaryEmails = auxiliaryEmails;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerUpdateDto)) {
            return false;
        }
        InfluencerUpdateDto other = (InfluencerUpdateDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$version = this.getVersion();
        Integer other$version = other.getVersion();
        if (this$version == null ? other$version != null : !((Object)this$version).equals(other$version)) {
            return false;
        }
        Integer this$gender = this.getGender();
        Integer other$gender = other.getGender();
        if (this$gender == null ? other$gender != null : !((Object)this$gender).equals(other$gender)) {
            return false;
        }
        Boolean this$isPaid = this.getIsPaid();
        Boolean other$isPaid = other.getIsPaid();
        if (this$isPaid == null ? other$isPaid != null : !((Object)this$isPaid).equals(other$isPaid)) {
            return false;
        }
        Long this$defaultSocialId = this.getDefaultSocialId();
        Long other$defaultSocialId = other.getDefaultSocialId();
        if (this$defaultSocialId == null ? other$defaultSocialId != null : !((Object)this$defaultSocialId).equals(other$defaultSocialId)) {
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
        String this$avatarUrl = this.getAvatarUrl();
        String other$avatarUrl = other.getAvatarUrl();
        if (this$avatarUrl == null ? other$avatarUrl != null : !this$avatarUrl.equals(other$avatarUrl)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        String this$country = this.getCountry();
        String other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
            return false;
        }
        String this$language = this.getLanguage();
        String other$language = other.getLanguage();
        if (this$language == null ? other$language != null : !this$language.equals(other$language)) {
            return false;
        }
        String this$race = this.getRace();
        String other$race = other.getRace();
        if (this$race == null ? other$race != null : !this$race.equals(other$race)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$source = this.getSource();
        String other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
            return false;
        }
        String this$influencerType = this.getInfluencerType();
        String other$influencerType = other.getInfluencerType();
        if (this$influencerType == null ? other$influencerType != null : !this$influencerType.equals(other$influencerType)) {
            return false;
        }
        String this$brand = this.getBrand();
        String other$brand = other.getBrand();
        if (this$brand == null ? other$brand != null : !this$brand.equals(other$brand)) {
            return false;
        }
        List this$tagIds = this.getTagIds();
        List other$tagIds = other.getTagIds();
        if (this$tagIds == null ? other$tagIds != null : !((Object)this$tagIds).equals(other$tagIds)) {
            return false;
        }
        BigDecimal this$commissionRate = this.getCommissionRate();
        BigDecimal other$commissionRate = other.getCommissionRate();
        if (this$commissionRate == null ? other$commissionRate != null : !((Object)this$commissionRate).equals(other$commissionRate)) {
            return false;
        }
        String this$paymentMethod = this.getPaymentMethod();
        String other$paymentMethod = other.getPaymentMethod();
        if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod)) {
            return false;
        }
        String this$paymentAccount = this.getPaymentAccount();
        String other$paymentAccount = other.getPaymentAccount();
        return !(this$paymentAccount == null ? other$paymentAccount != null : !this$paymentAccount.equals(other$paymentAccount));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerUpdateDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : ((Object)$version).hashCode());
        Integer $gender = this.getGender();
        result = result * 59 + ($gender == null ? 43 : ((Object)$gender).hashCode());
        Boolean $isPaid = this.getIsPaid();
        result = result * 59 + ($isPaid == null ? 43 : ((Object)$isPaid).hashCode());
        Long $defaultSocialId = this.getDefaultSocialId();
        result = result * 59 + ($defaultSocialId == null ? 43 : ((Object)$defaultSocialId).hashCode());
        Long $ownerId = this.getOwnerId();
        result = result * 59 + ($ownerId == null ? 43 : ((Object)$ownerId).hashCode());
        Long $contactPersonId = this.getContactPersonId();
        result = result * 59 + ($contactPersonId == null ? 43 : ((Object)$contactPersonId).hashCode());
        String $realName = this.getRealName();
        result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
        String $nickName = this.getNickName();
        result = result * 59 + ($nickName == null ? 43 : $nickName.hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        String $language = this.getLanguage();
        result = result * 59 + ($language == null ? 43 : $language.hashCode());
        String $race = this.getRace();
        result = result * 59 + ($race == null ? 43 : $race.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        String $influencerType = this.getInfluencerType();
        result = result * 59 + ($influencerType == null ? 43 : $influencerType.hashCode());
        String $brand = this.getBrand();
        result = result * 59 + ($brand == null ? 43 : $brand.hashCode());
        List $tagIds = this.getTagIds();
        result = result * 59 + ($tagIds == null ? 43 : ((Object)$tagIds).hashCode());
        BigDecimal $commissionRate = this.getCommissionRate();
        result = result * 59 + ($commissionRate == null ? 43 : ((Object)$commissionRate).hashCode());
        String $paymentMethod = this.getPaymentMethod();
        result = result * 59 + ($paymentMethod == null ? 43 : $paymentMethod.hashCode());
        String $paymentAccount = this.getPaymentAccount();
        result = result * 59 + ($paymentAccount == null ? 43 : $paymentAccount.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerUpdateDto(id=" + this.getId() + ", version=" + this.getVersion() + ", realName=" + this.getRealName() + ", nickName=" + this.getNickName() + ", avatarUrl=" + this.getAvatarUrl() + ", email=" + this.getEmail() + ", phone=" + this.getPhone() + ", country=" + this.getCountry() + ", language=" + this.getLanguage() + ", race=" + this.getRace() + ", gender=" + this.getGender() + ", description=" + this.getDescription() + ", source=" + this.getSource() + ", influencerType=" + this.getInfluencerType() + ", isPaid=" + this.getIsPaid() + ", brand=" + this.getBrand() + ", tagIds=" + this.getTagIds() + ", commissionRate=" + this.getCommissionRate() + ", paymentMethod=" + this.getPaymentMethod() + ", paymentAccount=" + this.getPaymentAccount() + ", defaultSocialId=" + this.getDefaultSocialId() + ", ownerId=" + this.getOwnerId() + ", contactPersonId=" + this.getContactPersonId() + ")";
    }
}

