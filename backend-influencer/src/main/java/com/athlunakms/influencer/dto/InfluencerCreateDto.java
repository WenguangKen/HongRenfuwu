package com.athlunakms.influencer.dto;

import com.athlunakms.influencer.dto.SocialMediaDto;
import java.math.BigDecimal;
import java.util.List;

public class InfluencerCreateDto {
    private String realName;
    private String email;
    private String nickName;
    private String avatarUrl;
    private String phone;
    private String country;
    private String language;
    private String race;
    private Integer gender;
    private String description;
    private String influencerType;
    private String source;
    private Long ownerId;
    private Long contactPersonId;
    private Boolean isPaid;
    private String brand;
    private List<Long> tagIds;
    private BigDecimal commissionRate;
    private String paymentMethod;
    private String paymentAccount;
    private String defaultPlatform;
    private String defaultHandle;
    private String defaultProfileUrl;
    private Long defaultFollowerCount;
    private List<SocialMediaDto> socialMediaList;
    private String address;
    private String city;
    private String state;
    private String street1;
    private String street2;
    private String postalCode;
    private String initialStage;
    private String initialStatus;
    private String auxiliaryEmails;

    public String getRealName() {
        return this.realName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNickName() {
        return this.nickName;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
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

    public String getInfluencerType() {
        return this.influencerType;
    }

    public String getSource() {
        return this.source;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
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

    public String getDefaultPlatform() {
        return this.defaultPlatform;
    }

    public String getDefaultHandle() {
        return this.defaultHandle;
    }

    public String getDefaultProfileUrl() {
        return this.defaultProfileUrl;
    }

    public Long getDefaultFollowerCount() {
        return this.defaultFollowerCount;
    }

    public List<SocialMediaDto> getSocialMediaList() {
        return this.socialMediaList;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getStreet1() {
        return this.street1;
    }

    public String getStreet2() {
        return this.street2;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public String getInitialStage() {
        return this.initialStage;
    }

    public String getInitialStatus() {
        return this.initialStatus;
    }

    public String getAuxiliaryEmails() {
        return this.auxiliaryEmails;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public void setInfluencerType(String influencerType) {
        this.influencerType = influencerType;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
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

    public void setDefaultPlatform(String defaultPlatform) {
        this.defaultPlatform = defaultPlatform;
    }

    public void setDefaultHandle(String defaultHandle) {
        this.defaultHandle = defaultHandle;
    }

    public void setDefaultProfileUrl(String defaultProfileUrl) {
        this.defaultProfileUrl = defaultProfileUrl;
    }

    public void setDefaultFollowerCount(Long defaultFollowerCount) {
        this.defaultFollowerCount = defaultFollowerCount;
    }

    public void setSocialMediaList(List<SocialMediaDto> socialMediaList) {
        this.socialMediaList = socialMediaList;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setInitialStage(String initialStage) {
        this.initialStage = initialStage;
    }

    public void setInitialStatus(String initialStatus) {
        this.initialStatus = initialStatus;
    }

    public void setAuxiliaryEmails(String auxiliaryEmails) {
        this.auxiliaryEmails = auxiliaryEmails;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerCreateDto)) {
            return false;
        }
        InfluencerCreateDto other = (InfluencerCreateDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$gender = this.getGender();
        Integer other$gender = other.getGender();
        if (this$gender == null ? other$gender != null : !((Object)this$gender).equals(other$gender)) {
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
        Boolean this$isPaid = this.getIsPaid();
        Boolean other$isPaid = other.getIsPaid();
        if (this$isPaid == null ? other$isPaid != null : !((Object)this$isPaid).equals(other$isPaid)) {
            return false;
        }
        Long this$defaultFollowerCount = this.getDefaultFollowerCount();
        Long other$defaultFollowerCount = other.getDefaultFollowerCount();
        if (this$defaultFollowerCount == null ? other$defaultFollowerCount != null : !((Object)this$defaultFollowerCount).equals(other$defaultFollowerCount)) {
            return false;
        }
        String this$realName = this.getRealName();
        String other$realName = other.getRealName();
        if (this$realName == null ? other$realName != null : !this$realName.equals(other$realName)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
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
        String this$influencerType = this.getInfluencerType();
        String other$influencerType = other.getInfluencerType();
        if (this$influencerType == null ? other$influencerType != null : !this$influencerType.equals(other$influencerType)) {
            return false;
        }
        String this$source = this.getSource();
        String other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
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
        if (this$paymentAccount == null ? other$paymentAccount != null : !this$paymentAccount.equals(other$paymentAccount)) {
            return false;
        }
        String this$defaultPlatform = this.getDefaultPlatform();
        String other$defaultPlatform = other.getDefaultPlatform();
        if (this$defaultPlatform == null ? other$defaultPlatform != null : !this$defaultPlatform.equals(other$defaultPlatform)) {
            return false;
        }
        String this$defaultHandle = this.getDefaultHandle();
        String other$defaultHandle = other.getDefaultHandle();
        if (this$defaultHandle == null ? other$defaultHandle != null : !this$defaultHandle.equals(other$defaultHandle)) {
            return false;
        }
        String this$defaultProfileUrl = this.getDefaultProfileUrl();
        String other$defaultProfileUrl = other.getDefaultProfileUrl();
        if (this$defaultProfileUrl == null ? other$defaultProfileUrl != null : !this$defaultProfileUrl.equals(other$defaultProfileUrl)) {
            return false;
        }
        List this$socialMediaList = this.getSocialMediaList();
        List other$socialMediaList = other.getSocialMediaList();
        if (this$socialMediaList == null ? other$socialMediaList != null : !((Object)this$socialMediaList).equals(other$socialMediaList)) {
            return false;
        }
        String this$address = this.getAddress();
        String other$address = other.getAddress();
        if (this$address == null ? other$address != null : !this$address.equals(other$address)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        String this$state = this.getState();
        String other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        String this$street1 = this.getStreet1();
        String other$street1 = other.getStreet1();
        if (this$street1 == null ? other$street1 != null : !this$street1.equals(other$street1)) {
            return false;
        }
        String this$street2 = this.getStreet2();
        String other$street2 = other.getStreet2();
        if (this$street2 == null ? other$street2 != null : !this$street2.equals(other$street2)) {
            return false;
        }
        String this$postalCode = this.getPostalCode();
        String other$postalCode = other.getPostalCode();
        if (this$postalCode == null ? other$postalCode != null : !this$postalCode.equals(other$postalCode)) {
            return false;
        }
        String this$initialStage = this.getInitialStage();
        String other$initialStage = other.getInitialStage();
        if (this$initialStage == null ? other$initialStage != null : !this$initialStage.equals(other$initialStage)) {
            return false;
        }
        String this$initialStatus = this.getInitialStatus();
        String other$initialStatus = other.getInitialStatus();
        return !(this$initialStatus == null ? other$initialStatus != null : !this$initialStatus.equals(other$initialStatus));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerCreateDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $gender = this.getGender();
        result = result * 59 + ($gender == null ? 43 : ((Object)$gender).hashCode());
        Long $ownerId = this.getOwnerId();
        result = result * 59 + ($ownerId == null ? 43 : ((Object)$ownerId).hashCode());
        Long $contactPersonId = this.getContactPersonId();
        result = result * 59 + ($contactPersonId == null ? 43 : ((Object)$contactPersonId).hashCode());
        Boolean $isPaid = this.getIsPaid();
        result = result * 59 + ($isPaid == null ? 43 : ((Object)$isPaid).hashCode());
        Long $defaultFollowerCount = this.getDefaultFollowerCount();
        result = result * 59 + ($defaultFollowerCount == null ? 43 : ((Object)$defaultFollowerCount).hashCode());
        String $realName = this.getRealName();
        result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $nickName = this.getNickName();
        result = result * 59 + ($nickName == null ? 43 : $nickName.hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
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
        String $influencerType = this.getInfluencerType();
        result = result * 59 + ($influencerType == null ? 43 : $influencerType.hashCode());
        String $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
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
        String $defaultPlatform = this.getDefaultPlatform();
        result = result * 59 + ($defaultPlatform == null ? 43 : $defaultPlatform.hashCode());
        String $defaultHandle = this.getDefaultHandle();
        result = result * 59 + ($defaultHandle == null ? 43 : $defaultHandle.hashCode());
        String $defaultProfileUrl = this.getDefaultProfileUrl();
        result = result * 59 + ($defaultProfileUrl == null ? 43 : $defaultProfileUrl.hashCode());
        List $socialMediaList = this.getSocialMediaList();
        result = result * 59 + ($socialMediaList == null ? 43 : ((Object)$socialMediaList).hashCode());
        String $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        String $street1 = this.getStreet1();
        result = result * 59 + ($street1 == null ? 43 : $street1.hashCode());
        String $street2 = this.getStreet2();
        result = result * 59 + ($street2 == null ? 43 : $street2.hashCode());
        String $postalCode = this.getPostalCode();
        result = result * 59 + ($postalCode == null ? 43 : $postalCode.hashCode());
        String $initialStage = this.getInitialStage();
        result = result * 59 + ($initialStage == null ? 43 : $initialStage.hashCode());
        String $initialStatus = this.getInitialStatus();
        result = result * 59 + ($initialStatus == null ? 43 : $initialStatus.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerCreateDto(realName=" + this.getRealName() + ", email=" + this.getEmail() + ", nickName=" + this.getNickName() + ", avatarUrl=" + this.getAvatarUrl() + ", phone=" + this.getPhone() + ", country=" + this.getCountry() + ", language=" + this.getLanguage() + ", race=" + this.getRace() + ", gender=" + this.getGender() + ", description=" + this.getDescription() + ", influencerType=" + this.getInfluencerType() + ", source=" + this.getSource() + ", ownerId=" + this.getOwnerId() + ", contactPersonId=" + this.getContactPersonId() + ", isPaid=" + this.getIsPaid() + ", brand=" + this.getBrand() + ", tagIds=" + this.getTagIds() + ", commissionRate=" + this.getCommissionRate() + ", paymentMethod=" + this.getPaymentMethod() + ", paymentAccount=" + this.getPaymentAccount() + ", defaultPlatform=" + this.getDefaultPlatform() + ", defaultHandle=" + this.getDefaultHandle() + ", defaultProfileUrl=" + this.getDefaultProfileUrl() + ", defaultFollowerCount=" + this.getDefaultFollowerCount() + ", socialMediaList=" + this.getSocialMediaList() + ", address=" + this.getAddress() + ", city=" + this.getCity() + ", state=" + this.getState() + ", street1=" + this.getStreet1() + ", street2=" + this.getStreet2() + ", postalCode=" + this.getPostalCode() + ", initialStage=" + this.getInitialStage() + ", initialStatus=" + this.getInitialStatus() + ")";
    }
}

