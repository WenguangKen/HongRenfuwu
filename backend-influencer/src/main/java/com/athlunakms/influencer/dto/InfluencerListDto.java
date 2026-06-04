package com.athlunakms.influencer.dto;

import com.athlunakms.influencer.dto.SocialMediaDto;
import com.athlunakms.influencer.entity.Influencer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InfluencerListDto {
    private Long id;
    private String uniqueName;
    private String realName;
    private String nickName;
    private String avatarUrl;
    private String country;
    private String race;
    private String platformName;
    private String profileLink;
    private String defaultHandle;
    private Long followerCount;
    private List<SocialMediaDto> socialMedias;
    private Influencer.Stage stage;
    private Influencer.Status status;
    private String brand;
    private Boolean isPaid;
    private String email;
    private String backupEmail;
    private String phone;
    private String source;
    private String sourceValue;
    private String influencerType;
    private BigDecimal commissionRate;
    private Integer totalOrders;
    private Integer sampleOrderCount;
    private Integer conversionOrderCount;
    private Integer totalFans;
    private Long ownerId;
    private String ownerName;
    private Long contactPersonId;
    private String contactPersonName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSampleAt;
    private LocalDateTime lastConversionAt;
    private List<Long> tagIds;
    private List<String> tags;
    private String description;
    private String auditorName;
    private LocalDateTime auditTime;
    private List<String> discountCodes;
    private BigDecimal paymentAmount;
    private Boolean hasContent;
    private Integer discountCodeCount;
    private Integer sampleCount;

    public Long getId() {
        return this.id;
    }

    public String getUniqueName() {
        return this.uniqueName;
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

    public String getCountry() {
        return this.country;
    }

    public String getRace() {
        return this.race;
    }

    public String getPlatformName() {
        return this.platformName;
    }

    public String getProfileLink() {
        return this.profileLink;
    }

    public String getDefaultHandle() {
        return this.defaultHandle;
    }

    public Long getFollowerCount() {
        return this.followerCount;
    }

    public List<SocialMediaDto> getSocialMedias() {
        return this.socialMedias;
    }

    public Influencer.Stage getStage() {
        return this.stage;
    }

    public Influencer.Status getStatus() {
        return this.status;
    }

    public String getBrand() {
        return this.brand;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public String getEmail() {
        return this.email;
    }

    public String getBackupEmail() {
        return this.backupEmail;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getSource() {
        return this.source;
    }

    public String getSourceValue() {
        return this.sourceValue;
    }

    public String getInfluencerType() {
        return this.influencerType;
    }

    public BigDecimal getCommissionRate() {
        return this.commissionRate;
    }

    public Integer getTotalOrders() {
        return this.totalOrders;
    }

    public Integer getSampleOrderCount() {
        return this.sampleOrderCount;
    }

    public Integer getConversionOrderCount() {
        return this.conversionOrderCount;
    }

    public Integer getTotalFans() {
        return this.totalFans;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
    }

    public String getContactPersonName() {
        return this.contactPersonName;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getLastSampleAt() {
        return this.lastSampleAt;
    }

    public LocalDateTime getLastConversionAt() {
        return this.lastConversionAt;
    }

    public List<Long> getTagIds() {
        return this.tagIds;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuditorName() {
        return this.auditorName;
    }

    public LocalDateTime getAuditTime() {
        return this.auditTime;
    }

    public List<String> getDiscountCodes() {
        return this.discountCodes;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    public Boolean getHasContent() {
        return this.hasContent;
    }

    public Integer getDiscountCodeCount() {
        return this.discountCodeCount;
    }

    public Integer getSampleCount() {
        return this.sampleCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
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

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public void setDefaultHandle(String defaultHandle) {
        this.defaultHandle = defaultHandle;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public void setSocialMedias(List<SocialMediaDto> socialMedias) {
        this.socialMedias = socialMedias;
    }

    public void setStage(Influencer.Stage stage) {
        this.stage = stage;
    }

    public void setStatus(Influencer.Status status) {
        this.status = status;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBackupEmail(String backupEmail) {
        this.backupEmail = backupEmail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public void setInfluencerType(String influencerType) {
        this.influencerType = influencerType;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public void setSampleOrderCount(Integer sampleOrderCount) {
        this.sampleOrderCount = sampleOrderCount;
    }

    public void setConversionOrderCount(Integer conversionOrderCount) {
        this.conversionOrderCount = conversionOrderCount;
    }

    public void setTotalFans(Integer totalFans) {
        this.totalFans = totalFans;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLastSampleAt(LocalDateTime lastSampleAt) {
        this.lastSampleAt = lastSampleAt;
    }

    public void setLastConversionAt(LocalDateTime lastConversionAt) {
        this.lastConversionAt = lastConversionAt;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public void setDiscountCodes(List<String> discountCodes) {
        this.discountCodes = discountCodes;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setHasContent(Boolean hasContent) {
        this.hasContent = hasContent;
    }

    public void setDiscountCodeCount(Integer discountCodeCount) {
        this.discountCodeCount = discountCodeCount;
    }

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerListDto)) {
            return false;
        }
        InfluencerListDto other = (InfluencerListDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$followerCount = this.getFollowerCount();
        Long other$followerCount = other.getFollowerCount();
        if (this$followerCount == null ? other$followerCount != null : !((Object)this$followerCount).equals(other$followerCount)) {
            return false;
        }
        Boolean this$isPaid = this.getIsPaid();
        Boolean other$isPaid = other.getIsPaid();
        if (this$isPaid == null ? other$isPaid != null : !((Object)this$isPaid).equals(other$isPaid)) {
            return false;
        }
        Integer this$totalOrders = this.getTotalOrders();
        Integer other$totalOrders = other.getTotalOrders();
        if (this$totalOrders == null ? other$totalOrders != null : !((Object)this$totalOrders).equals(other$totalOrders)) {
            return false;
        }
        Integer this$sampleOrderCount = this.getSampleOrderCount();
        Integer other$sampleOrderCount = other.getSampleOrderCount();
        if (this$sampleOrderCount == null ? other$sampleOrderCount != null : !((Object)this$sampleOrderCount).equals(other$sampleOrderCount)) {
            return false;
        }
        Integer this$conversionOrderCount = this.getConversionOrderCount();
        Integer other$conversionOrderCount = other.getConversionOrderCount();
        if (this$conversionOrderCount == null ? other$conversionOrderCount != null : !((Object)this$conversionOrderCount).equals(other$conversionOrderCount)) {
            return false;
        }
        Integer this$totalFans = this.getTotalFans();
        Integer other$totalFans = other.getTotalFans();
        if (this$totalFans == null ? other$totalFans != null : !((Object)this$totalFans).equals(other$totalFans)) {
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
        Boolean this$hasContent = this.getHasContent();
        Boolean other$hasContent = other.getHasContent();
        if (this$hasContent == null ? other$hasContent != null : !((Object)this$hasContent).equals(other$hasContent)) {
            return false;
        }
        Integer this$discountCodeCount = this.getDiscountCodeCount();
        Integer other$discountCodeCount = other.getDiscountCodeCount();
        if (this$discountCodeCount == null ? other$discountCodeCount != null : !((Object)this$discountCodeCount).equals(other$discountCodeCount)) {
            return false;
        }
        Integer this$sampleCount = this.getSampleCount();
        Integer other$sampleCount = other.getSampleCount();
        if (this$sampleCount == null ? other$sampleCount != null : !((Object)this$sampleCount).equals(other$sampleCount)) {
            return false;
        }
        String this$uniqueName = this.getUniqueName();
        String other$uniqueName = other.getUniqueName();
        if (this$uniqueName == null ? other$uniqueName != null : !this$uniqueName.equals(other$uniqueName)) {
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
        String this$country = this.getCountry();
        String other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
            return false;
        }
        String this$race = this.getRace();
        String other$race = other.getRace();
        if (this$race == null ? other$race != null : !this$race.equals(other$race)) {
            return false;
        }
        String this$platformName = this.getPlatformName();
        String other$platformName = other.getPlatformName();
        if (this$platformName == null ? other$platformName != null : !this$platformName.equals(other$platformName)) {
            return false;
        }
        String this$profileLink = this.getProfileLink();
        String other$profileLink = other.getProfileLink();
        if (this$profileLink == null ? other$profileLink != null : !this$profileLink.equals(other$profileLink)) {
            return false;
        }
        List this$socialMedias = this.getSocialMedias();
        List other$socialMedias = other.getSocialMedias();
        if (this$socialMedias == null ? other$socialMedias != null : !((Object)this$socialMedias).equals(other$socialMedias)) {
            return false;
        }
        Influencer.Stage this$stage = this.getStage();
        Influencer.Stage other$stage = other.getStage();
        if (this$stage == null ? other$stage != null : !this$stage.equals(other$stage)) {
            return false;
        }
        Influencer.Status this$status = this.getStatus();
        Influencer.Status other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$brand = this.getBrand();
        String other$brand = other.getBrand();
        if (this$brand == null ? other$brand != null : !this$brand.equals(other$brand)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$backupEmail = this.getBackupEmail();
        String other$backupEmail = other.getBackupEmail();
        if (this$backupEmail == null ? other$backupEmail != null : !this$backupEmail.equals(other$backupEmail)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        String this$source = this.getSource();
        String other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
            return false;
        }
        String this$sourceValue = this.getSourceValue();
        String other$sourceValue = other.getSourceValue();
        if (this$sourceValue == null ? other$sourceValue != null : !this$sourceValue.equals(other$sourceValue)) {
            return false;
        }
        String this$influencerType = this.getInfluencerType();
        String other$influencerType = other.getInfluencerType();
        if (this$influencerType == null ? other$influencerType != null : !this$influencerType.equals(other$influencerType)) {
            return false;
        }
        BigDecimal this$commissionRate = this.getCommissionRate();
        BigDecimal other$commissionRate = other.getCommissionRate();
        if (this$commissionRate == null ? other$commissionRate != null : !((Object)this$commissionRate).equals(other$commissionRate)) {
            return false;
        }
        String this$ownerName = this.getOwnerName();
        String other$ownerName = other.getOwnerName();
        if (this$ownerName == null ? other$ownerName != null : !this$ownerName.equals(other$ownerName)) {
            return false;
        }
        String this$contactPersonName = this.getContactPersonName();
        String other$contactPersonName = other.getContactPersonName();
        if (this$contactPersonName == null ? other$contactPersonName != null : !this$contactPersonName.equals(other$contactPersonName)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
            return false;
        }
        LocalDateTime this$lastSampleAt = this.getLastSampleAt();
        LocalDateTime other$lastSampleAt = other.getLastSampleAt();
        if (this$lastSampleAt == null ? other$lastSampleAt != null : !((Object)this$lastSampleAt).equals(other$lastSampleAt)) {
            return false;
        }
        LocalDateTime this$lastConversionAt = this.getLastConversionAt();
        LocalDateTime other$lastConversionAt = other.getLastConversionAt();
        if (this$lastConversionAt == null ? other$lastConversionAt != null : !((Object)this$lastConversionAt).equals(other$lastConversionAt)) {
            return false;
        }
        List this$tagIds = this.getTagIds();
        List other$tagIds = other.getTagIds();
        if (this$tagIds == null ? other$tagIds != null : !((Object)this$tagIds).equals(other$tagIds)) {
            return false;
        }
        List this$tags = this.getTags();
        List other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !((Object)this$tags).equals(other$tags)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        String this$auditorName = this.getAuditorName();
        String other$auditorName = other.getAuditorName();
        if (this$auditorName == null ? other$auditorName != null : !this$auditorName.equals(other$auditorName)) {
            return false;
        }
        LocalDateTime this$auditTime = this.getAuditTime();
        LocalDateTime other$auditTime = other.getAuditTime();
        if (this$auditTime == null ? other$auditTime != null : !((Object)this$auditTime).equals(other$auditTime)) {
            return false;
        }
        List this$discountCodes = this.getDiscountCodes();
        List other$discountCodes = other.getDiscountCodes();
        if (this$discountCodes == null ? other$discountCodes != null : !((Object)this$discountCodes).equals(other$discountCodes)) {
            return false;
        }
        BigDecimal this$paymentAmount = this.getPaymentAmount();
        BigDecimal other$paymentAmount = other.getPaymentAmount();
        return !(this$paymentAmount == null ? other$paymentAmount != null : !((Object)this$paymentAmount).equals(other$paymentAmount));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerListDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $followerCount = this.getFollowerCount();
        result = result * 59 + ($followerCount == null ? 43 : ((Object)$followerCount).hashCode());
        Boolean $isPaid = this.getIsPaid();
        result = result * 59 + ($isPaid == null ? 43 : ((Object)$isPaid).hashCode());
        Integer $totalOrders = this.getTotalOrders();
        result = result * 59 + ($totalOrders == null ? 43 : ((Object)$totalOrders).hashCode());
        Integer $sampleOrderCount = this.getSampleOrderCount();
        result = result * 59 + ($sampleOrderCount == null ? 43 : ((Object)$sampleOrderCount).hashCode());
        Integer $conversionOrderCount = this.getConversionOrderCount();
        result = result * 59 + ($conversionOrderCount == null ? 43 : ((Object)$conversionOrderCount).hashCode());
        Integer $totalFans = this.getTotalFans();
        result = result * 59 + ($totalFans == null ? 43 : ((Object)$totalFans).hashCode());
        Long $ownerId = this.getOwnerId();
        result = result * 59 + ($ownerId == null ? 43 : ((Object)$ownerId).hashCode());
        Long $contactPersonId = this.getContactPersonId();
        result = result * 59 + ($contactPersonId == null ? 43 : ((Object)$contactPersonId).hashCode());
        Boolean $hasContent = this.getHasContent();
        result = result * 59 + ($hasContent == null ? 43 : ((Object)$hasContent).hashCode());
        Integer $discountCodeCount = this.getDiscountCodeCount();
        result = result * 59 + ($discountCodeCount == null ? 43 : ((Object)$discountCodeCount).hashCode());
        Integer $sampleCount = this.getSampleCount();
        result = result * 59 + ($sampleCount == null ? 43 : ((Object)$sampleCount).hashCode());
        String $uniqueName = this.getUniqueName();
        result = result * 59 + ($uniqueName == null ? 43 : $uniqueName.hashCode());
        String $realName = this.getRealName();
        result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
        String $nickName = this.getNickName();
        result = result * 59 + ($nickName == null ? 43 : $nickName.hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
        String $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        String $race = this.getRace();
        result = result * 59 + ($race == null ? 43 : $race.hashCode());
        String $platformName = this.getPlatformName();
        result = result * 59 + ($platformName == null ? 43 : $platformName.hashCode());
        String $profileLink = this.getProfileLink();
        result = result * 59 + ($profileLink == null ? 43 : $profileLink.hashCode());
        List $socialMedias = this.getSocialMedias();
        result = result * 59 + ($socialMedias == null ? 43 : ((Object)$socialMedias).hashCode());
        Influencer.Stage $stage = this.getStage();
        result = result * 59 + ($stage == null ? 43 : $stage.hashCode());
        Influencer.Status $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $brand = this.getBrand();
        result = result * 59 + ($brand == null ? 43 : $brand.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $backupEmail = this.getBackupEmail();
        result = result * 59 + ($backupEmail == null ? 43 : $backupEmail.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        String $sourceValue = this.getSourceValue();
        result = result * 59 + ($sourceValue == null ? 43 : $sourceValue.hashCode());
        String $influencerType = this.getInfluencerType();
        result = result * 59 + ($influencerType == null ? 43 : $influencerType.hashCode());
        BigDecimal $commissionRate = this.getCommissionRate();
        result = result * 59 + ($commissionRate == null ? 43 : ((Object)$commissionRate).hashCode());
        String $ownerName = this.getOwnerName();
        result = result * 59 + ($ownerName == null ? 43 : $ownerName.hashCode());
        String $contactPersonName = this.getContactPersonName();
        result = result * 59 + ($contactPersonName == null ? 43 : $contactPersonName.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        LocalDateTime $lastSampleAt = this.getLastSampleAt();
        result = result * 59 + ($lastSampleAt == null ? 43 : ((Object)$lastSampleAt).hashCode());
        LocalDateTime $lastConversionAt = this.getLastConversionAt();
        result = result * 59 + ($lastConversionAt == null ? 43 : ((Object)$lastConversionAt).hashCode());
        List $tagIds = this.getTagIds();
        result = result * 59 + ($tagIds == null ? 43 : ((Object)$tagIds).hashCode());
        List $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : ((Object)$tags).hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $auditorName = this.getAuditorName();
        result = result * 59 + ($auditorName == null ? 43 : $auditorName.hashCode());
        LocalDateTime $auditTime = this.getAuditTime();
        result = result * 59 + ($auditTime == null ? 43 : ((Object)$auditTime).hashCode());
        List $discountCodes = this.getDiscountCodes();
        result = result * 59 + ($discountCodes == null ? 43 : ((Object)$discountCodes).hashCode());
        BigDecimal $paymentAmount = this.getPaymentAmount();
        result = result * 59 + ($paymentAmount == null ? 43 : ((Object)$paymentAmount).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerListDto(id=" + this.getId() + ", uniqueName=" + this.getUniqueName() + ", realName=" + this.getRealName() + ", nickName=" + this.getNickName() + ", avatarUrl=" + this.getAvatarUrl() + ", country=" + this.getCountry() + ", race=" + this.getRace() + ", platformName=" + this.getPlatformName() + ", profileLink=" + this.getProfileLink() + ", followerCount=" + this.getFollowerCount() + ", socialMedias=" + this.getSocialMedias() + ", stage=" + this.getStage() + ", status=" + this.getStatus() + ", brand=" + this.getBrand() + ", isPaid=" + this.getIsPaid() + ", email=" + this.getEmail() + ", backupEmail=" + this.getBackupEmail() + ", phone=" + this.getPhone() + ", source=" + this.getSource() + ", sourceValue=" + this.getSourceValue() + ", influencerType=" + this.getInfluencerType() + ", commissionRate=" + this.getCommissionRate() + ", totalOrders=" + this.getTotalOrders() + ", sampleOrderCount=" + this.getSampleOrderCount() + ", conversionOrderCount=" + this.getConversionOrderCount() + ", totalFans=" + this.getTotalFans() + ", ownerId=" + this.getOwnerId() + ", ownerName=" + this.getOwnerName() + ", contactPersonId=" + this.getContactPersonId() + ", contactPersonName=" + this.getContactPersonName() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", lastSampleAt=" + this.getLastSampleAt() + ", lastConversionAt=" + this.getLastConversionAt() + ", tagIds=" + this.getTagIds() + ", tags=" + this.getTags() + ", description=" + this.getDescription() + ", auditorName=" + this.getAuditorName() + ", auditTime=" + this.getAuditTime() + ", discountCodes=" + this.getDiscountCodes() + ", paymentAmount=" + this.getPaymentAmount() + ", hasContent=" + this.getHasContent() + ", discountCodeCount=" + this.getDiscountCodeCount() + ", sampleCount=" + this.getSampleCount() + ")";
    }
}

