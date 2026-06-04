package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name="influencer", indexes={@Index(name="idx_influencer_stage_status_created", columnList="stage, status, created_at DESC"), @Index(name="idx_influencer_owner", columnList="owner_id"), @Index(name="idx_influencer_contact", columnList="contact_person_id"), @Index(name="idx_influencer_platform", columnList="default_platform")})
@SQLRestriction("is_deleted = false")
public class Influencer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="real_name")
    private String realName;
    @Column(name="nick_name")
    private String nickName;
    @Column(name="avatar_url")
    private String avatarUrl;
    private String email;
    private String phone;
    private String country;
    private String language;
    private String race;
    @Column(name="gender")
    private Integer gender;
    @Column(columnDefinition="TEXT")
    private String description;
    @Column(nullable=false)
    @Enumerated(value=EnumType.STRING)
    private Origin origin = Origin.MANUAL;
    @Column(name="is_paid")
    private Boolean isPaid = false;
    private String brand;
    @Column(name="source")
    private String source;
    @Column(name="source_value")
    private String sourceValue;
    @Column(name="influencer_type")
    private String influencerType;
    @Column(columnDefinition="json")
    private String tags;
    @Column(name="backup_email")
    private String backupEmail;
    @Column(name="auditor_name")
    private String auditorName;
    @Column(name="audit_time")
    private LocalDateTime auditTime;
    @Column(name="platform")
    private String platform;
    @Column(nullable=false, length=50)
    @Enumerated(value=EnumType.STRING)
    private Stage stage = Stage.POOL;
    @Column(nullable=false, length=50)
    @Enumerated(value=EnumType.STRING)
    private Status status = Status.PENDING;
    @Column(name="commission_rate")
    private BigDecimal commissionRate;
    @Column(name="payment_method")
    private String paymentMethod;
    @Column(name="payment_account")
    private String paymentAccount;
    @Column(name="default_social_id")
    private Long defaultSocialId;
    @Column(name="default_platform")
    private String defaultPlatform;
    @Column(name="default_handle")
    private String defaultHandle;
    @Column(name="default_url")
    private String defaultUrl;
    @Column(name="owner_id")
    private Long ownerId;
    @Column(name="contact_person_id")
    private Long contactPersonId;
    @Column(name="total_fans")
    private Long totalFans = 0L;
    @Column(name="total_orders")
    private Integer totalOrders = 0;
    @Column(name="has_content")
    private Boolean hasContent = false;
    @Column(name="last_activity_at")
    private LocalDateTime lastActivityAt;
    @Column(name="last_sample_at")
    private LocalDateTime lastSampleAt;
    @Column(name="last_conversion_at")
    private LocalDateTime lastConversionAt;
    @Column(name="last_content_at")
    private LocalDateTime lastContentAt;
    @Column(name="last_manual_restore_at")
    private LocalDateTime lastManualRestoreAt;
    @Column(name="has_pending_sample")
    private Boolean hasPendingSample = false;
    @Column(name="has_pending_content")
    private Boolean hasPendingContent = false;
    @Version
    private Integer version;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    @Column(name="is_deleted", nullable = false)
    private Boolean isDeleted = false;
    @Transient
    private InfluencerAddress addressInfo;
    @Transient
    private List<SocialMedia> socialMediaList;

    public Long getId() {
        return this.id;
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

    public Origin getOrigin() {
        return this.origin;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public String getBrand() {
        return this.brand;
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

    public String getTags() {
        return this.tags;
    }

    public String getBackupEmail() {
        return this.backupEmail;
    }

    public String getAuditorName() {
        return this.auditorName;
    }

    public LocalDateTime getAuditTime() {
        return this.auditTime;
    }

    public String getPlatform() {
        return this.platform;
    }

    public Stage getStage() {
        return this.stage;
    }

    public Status getStatus() {
        return this.status;
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

    public String getDefaultPlatform() {
        return this.defaultPlatform;
    }

    public String getDefaultHandle() {
        return this.defaultHandle;
    }

    public String getDefaultUrl() {
        return this.defaultUrl;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
    }

    public Long getTotalFans() {
        return this.totalFans;
    }

    public Integer getTotalOrders() {
        return this.totalOrders;
    }

    public Boolean getHasContent() {
        return this.hasContent;
    }

    public LocalDateTime getLastActivityAt() {
        return this.lastActivityAt;
    }

    public LocalDateTime getLastSampleAt() {
        return this.lastSampleAt;
    }

    public LocalDateTime getLastConversionAt() {
        return this.lastConversionAt;
    }

    public LocalDateTime getLastContentAt() {
        return this.lastContentAt;
    }

    public LocalDateTime getLastManualRestoreAt() {
        return this.lastManualRestoreAt;
    }

    public Boolean getHasPendingSample() {
        return this.hasPendingSample;
    }

    public Boolean getHasPendingContent() {
        return this.hasPendingContent;
    }

    public Integer getVersion() {
        return this.version;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public InfluencerAddress getAddressInfo() {
        return this.addressInfo;
    }

    public List<SocialMedia> getSocialMediaList() {
        return this.socialMediaList;
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

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setBackupEmail(String backupEmail) {
        this.backupEmail = backupEmail;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public void setDefaultPlatform(String defaultPlatform) {
        this.defaultPlatform = defaultPlatform;
    }

    public void setDefaultHandle(String defaultHandle) {
        this.defaultHandle = defaultHandle;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setTotalFans(Long totalFans) {
        this.totalFans = totalFans;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public void setHasContent(Boolean hasContent) {
        this.hasContent = hasContent;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public void setLastSampleAt(LocalDateTime lastSampleAt) {
        this.lastSampleAt = lastSampleAt;
    }

    public void setLastConversionAt(LocalDateTime lastConversionAt) {
        this.lastConversionAt = lastConversionAt;
    }

    public void setLastContentAt(LocalDateTime lastContentAt) {
        this.lastContentAt = lastContentAt;
    }

    public void setLastManualRestoreAt(LocalDateTime lastManualRestoreAt) {
        this.lastManualRestoreAt = lastManualRestoreAt;
    }

    public void setHasPendingSample(Boolean hasPendingSample) {
        this.hasPendingSample = hasPendingSample;
    }

    public void setHasPendingContent(Boolean hasPendingContent) {
        this.hasPendingContent = hasPendingContent;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setAddressInfo(InfluencerAddress addressInfo) {
        this.addressInfo = addressInfo;
    }

    public void setSocialMediaList(List<SocialMedia> socialMediaList) {
        this.socialMediaList = socialMediaList;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Influencer)) {
            return false;
        }
        Influencer other = (Influencer)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
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
        Long this$totalFans = this.getTotalFans();
        Long other$totalFans = other.getTotalFans();
        if (this$totalFans == null ? other$totalFans != null : !((Object)this$totalFans).equals(other$totalFans)) {
            return false;
        }
        Integer this$totalOrders = this.getTotalOrders();
        Integer other$totalOrders = other.getTotalOrders();
        if (this$totalOrders == null ? other$totalOrders != null : !((Object)this$totalOrders).equals(other$totalOrders)) {
            return false;
        }
        Boolean this$hasContent = this.getHasContent();
        Boolean other$hasContent = other.getHasContent();
        if (this$hasContent == null ? other$hasContent != null : !((Object)this$hasContent).equals(other$hasContent)) {
            return false;
        }
        Boolean this$hasPendingSample = this.getHasPendingSample();
        Boolean other$hasPendingSample = other.getHasPendingSample();
        if (this$hasPendingSample == null ? other$hasPendingSample != null : !((Object)this$hasPendingSample).equals(other$hasPendingSample)) {
            return false;
        }
        Boolean this$hasPendingContent = this.getHasPendingContent();
        Boolean other$hasPendingContent = other.getHasPendingContent();
        if (this$hasPendingContent == null ? other$hasPendingContent != null : !((Object)this$hasPendingContent).equals(other$hasPendingContent)) {
            return false;
        }
        Integer this$version = this.getVersion();
        Integer other$version = other.getVersion();
        if (this$version == null ? other$version != null : !((Object)this$version).equals(other$version)) {
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
        Origin this$origin = this.getOrigin();
        Origin other$origin = other.getOrigin();
        if (this$origin == null ? other$origin != null : !this$origin.equals(other$origin)) {
            return false;
        }
        String this$brand = this.getBrand();
        String other$brand = other.getBrand();
        if (this$brand == null ? other$brand != null : !this$brand.equals(other$brand)) {
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
        String this$tags = this.getTags();
        String other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) {
            return false;
        }
        String this$backupEmail = this.getBackupEmail();
        String other$backupEmail = other.getBackupEmail();
        if (this$backupEmail == null ? other$backupEmail != null : !this$backupEmail.equals(other$backupEmail)) {
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
        String this$platform = this.getPlatform();
        String other$platform = other.getPlatform();
        if (this$platform == null ? other$platform != null : !this$platform.equals(other$platform)) {
            return false;
        }
        Stage this$stage = this.getStage();
        Stage other$stage = other.getStage();
        if (this$stage == null ? other$stage != null : !this$stage.equals(other$stage)) {
            return false;
        }
        Status this$status = this.getStatus();
        Status other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
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
        String this$defaultUrl = this.getDefaultUrl();
        String other$defaultUrl = other.getDefaultUrl();
        if (this$defaultUrl == null ? other$defaultUrl != null : !this$defaultUrl.equals(other$defaultUrl)) {
            return false;
        }
        LocalDateTime this$lastActivityAt = this.getLastActivityAt();
        LocalDateTime other$lastActivityAt = other.getLastActivityAt();
        if (this$lastActivityAt == null ? other$lastActivityAt != null : !((Object)this$lastActivityAt).equals(other$lastActivityAt)) {
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
        LocalDateTime this$lastContentAt = this.getLastContentAt();
        LocalDateTime other$lastContentAt = other.getLastContentAt();
        if (this$lastContentAt == null ? other$lastContentAt != null : !((Object)this$lastContentAt).equals(other$lastContentAt)) {
            return false;
        }
        LocalDateTime this$lastManualRestoreAt = this.getLastManualRestoreAt();
        LocalDateTime other$lastManualRestoreAt = other.getLastManualRestoreAt();
        if (this$lastManualRestoreAt == null ? other$lastManualRestoreAt != null : !((Object)this$lastManualRestoreAt).equals(other$lastManualRestoreAt)) {
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
        InfluencerAddress this$addressInfo = this.getAddressInfo();
        InfluencerAddress other$addressInfo = other.getAddressInfo();
        if (this$addressInfo == null ? other$addressInfo != null : !this$addressInfo.equals(other$addressInfo)) {
            return false;
        }
        List this$socialMediaList = this.getSocialMediaList();
        List other$socialMediaList = other.getSocialMediaList();
        return !(this$socialMediaList == null ? other$socialMediaList != null : !((Object)this$socialMediaList).equals(other$socialMediaList));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Influencer;
    }

    public int hashCode() {
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
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
        Long $totalFans = this.getTotalFans();
        result = result * 59 + ($totalFans == null ? 43 : ((Object)$totalFans).hashCode());
        Integer $totalOrders = this.getTotalOrders();
        result = result * 59 + ($totalOrders == null ? 43 : ((Object)$totalOrders).hashCode());
        Boolean $hasContent = this.getHasContent();
        result = result * 59 + ($hasContent == null ? 43 : ((Object)$hasContent).hashCode());
        Boolean $hasPendingSample = this.getHasPendingSample();
        result = result * 59 + ($hasPendingSample == null ? 43 : ((Object)$hasPendingSample).hashCode());
        Boolean $hasPendingContent = this.getHasPendingContent();
        result = result * 59 + ($hasPendingContent == null ? 43 : ((Object)$hasPendingContent).hashCode());
        Integer $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : ((Object)$version).hashCode());
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
        Origin $origin = this.getOrigin();
        result = result * 59 + ($origin == null ? 43 : $origin.hashCode());
        String $brand = this.getBrand();
        result = result * 59 + ($brand == null ? 43 : $brand.hashCode());
        String $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        String $sourceValue = this.getSourceValue();
        result = result * 59 + ($sourceValue == null ? 43 : $sourceValue.hashCode());
        String $influencerType = this.getInfluencerType();
        result = result * 59 + ($influencerType == null ? 43 : $influencerType.hashCode());
        String $tags = this.getTags();
        result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
        String $backupEmail = this.getBackupEmail();
        result = result * 59 + ($backupEmail == null ? 43 : $backupEmail.hashCode());
        String $auditorName = this.getAuditorName();
        result = result * 59 + ($auditorName == null ? 43 : $auditorName.hashCode());
        LocalDateTime $auditTime = this.getAuditTime();
        result = result * 59 + ($auditTime == null ? 43 : ((Object)$auditTime).hashCode());
        String $platform = this.getPlatform();
        result = result * 59 + ($platform == null ? 43 : $platform.hashCode());
        Stage $stage = this.getStage();
        result = result * 59 + ($stage == null ? 43 : $stage.hashCode());
        Status $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
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
        String $defaultUrl = this.getDefaultUrl();
        result = result * 59 + ($defaultUrl == null ? 43 : $defaultUrl.hashCode());
        LocalDateTime $lastActivityAt = this.getLastActivityAt();
        result = result * 59 + ($lastActivityAt == null ? 43 : ((Object)$lastActivityAt).hashCode());
        LocalDateTime $lastSampleAt = this.getLastSampleAt();
        result = result * 59 + ($lastSampleAt == null ? 43 : ((Object)$lastSampleAt).hashCode());
        LocalDateTime $lastConversionAt = this.getLastConversionAt();
        result = result * 59 + ($lastConversionAt == null ? 43 : ((Object)$lastConversionAt).hashCode());
        LocalDateTime $lastContentAt = this.getLastContentAt();
        result = result * 59 + ($lastContentAt == null ? 43 : ((Object)$lastContentAt).hashCode());
        LocalDateTime $lastManualRestoreAt = this.getLastManualRestoreAt();
        result = result * 59 + ($lastManualRestoreAt == null ? 43 : ((Object)$lastManualRestoreAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        InfluencerAddress $addressInfo = this.getAddressInfo();
        result = result * 59 + ($addressInfo == null ? 43 : $addressInfo.hashCode());
        List $socialMediaList = this.getSocialMediaList();
        result = result * 59 + ($socialMediaList == null ? 43 : ((Object)$socialMediaList).hashCode());
        return result;
    }

    public String toString() {
        return "Influencer(id=" + this.getId() + ", realName=" + this.getRealName() + ", nickName=" + this.getNickName() + ", avatarUrl=" + this.getAvatarUrl() + ", email=" + this.getEmail() + ", phone=" + this.getPhone() + ", country=" + this.getCountry() + ", language=" + this.getLanguage() + ", race=" + this.getRace() + ", gender=" + this.getGender() + ", description=" + this.getDescription() + ", origin=" + this.getOrigin() + ", isPaid=" + this.getIsPaid() + ", brand=" + this.getBrand() + ", source=" + this.getSource() + ", sourceValue=" + this.getSourceValue() + ", influencerType=" + this.getInfluencerType() + ", tags=" + this.getTags() + ", backupEmail=" + this.getBackupEmail() + ", auditorName=" + this.getAuditorName() + ", auditTime=" + this.getAuditTime() + ", platform=" + this.getPlatform() + ", stage=" + this.getStage() + ", status=" + this.getStatus() + ", commissionRate=" + this.getCommissionRate() + ", paymentMethod=" + this.getPaymentMethod() + ", paymentAccount=" + this.getPaymentAccount() + ", defaultSocialId=" + this.getDefaultSocialId() + ", defaultPlatform=" + this.getDefaultPlatform() + ", defaultHandle=" + this.getDefaultHandle() + ", defaultUrl=" + this.getDefaultUrl() + ", ownerId=" + this.getOwnerId() + ", contactPersonId=" + this.getContactPersonId() + ", totalFans=" + this.getTotalFans() + ", totalOrders=" + this.getTotalOrders() + ", hasContent=" + this.getHasContent() + ", lastActivityAt=" + this.getLastActivityAt() + ", lastSampleAt=" + this.getLastSampleAt() + ", lastConversionAt=" + this.getLastConversionAt() + ", lastContentAt=" + this.getLastContentAt() + ", lastManualRestoreAt=" + this.getLastManualRestoreAt() + ", hasPendingSample=" + this.getHasPendingSample() + ", hasPendingContent=" + this.getHasPendingContent() + ", version=" + this.getVersion() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", addressInfo=" + this.getAddressInfo() + ", socialMediaList=" + this.getSocialMediaList() + ")";
    }

    public enum Origin {
        MANUAL,
        IMPORT;
    }

    public enum Stage {
        POOL,
        ONBOARDED,
        TRASH;
    }

    public enum Status {
        PENDING,
        UNSCREENED,
        COMMUNICATING,
        CONTACTED,
        COOPERATING,
        DORMANT,
        BLACKLIST,
        PAUSED,
        TERMINATED,
        REJECTED;
    }
}

