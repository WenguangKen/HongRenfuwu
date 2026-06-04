/**
 * 红人列表筛选查询 DTO
 *
 * 用于接收前端传入的筛选参数支持多维度条件组合查询
 * 所有字段均可选null 表示不参与筛选
 */
package com.athlunakms.influencer.dto;

import com.athlunakms.influencer.entity.Influencer;
import java.util.List;

public class InfluencerFilterDto {
    private String searchKey;                  // 搜索关键词匹配姓名/昵称/邮箱
    private Influencer.Stage stage;            // 阶段筛选POOL/ONBOARDED/TRASH
    private Influencer.Status status;          // 状态筛选PENDING/COOPERATING 等
    private String country;                    // 国家筛选
    private List<String> platform;             // 建联平台筛选多选
    private List<String> socialPlatform;       // 社媒平台筛选多选
    private String brand;                      // 品牌筛选
    private Boolean isPaid;                    // 是否付费合作
    private List<Long> tagIds;                 // 红人标签 IDOR 关系
    private List<Long> liaisonTagIds;          // 联络员标签 ID
    private List<Long> ownerIds;                      // 负责人 ID 列表
    private List<Long> contactPersonIds;              // 联络人 ID 列表
    private String email;                      // 单个邮箱模糊搜索
    private String origin;                     // 数据来源IMPORT/CRAWLER/MANUAL
    private List<String> influencerType;       // 红人类型多选
    private String profileLink;                // 主页链接模糊搜索
    private String socialHandle;               // 社媒账号名搜索
    private List<String> race;                 // 人种/肤色筛选多选
    private String timeType;                   // 时间类型created/updated
    private String timeStart;                  // 时间范围起始yyyy-MM-dd
    private String timeEnd;                    // 时间范围结束yyyy-MM-dd
    private Integer fanRangeMin;               // 粉丝数下限
    private Integer fanRangeMax;               // 粉丝数上限
    private int page = 1;                      // 当前页码1-based
    private int size = 20;                     // 每页大小
    private Boolean hasOutputContent;          // 是否有输出内容
    private String discountCode;               // 折扣码搜索
    private Integer minSampleCount;            // 最小合作次数
    private Integer maxSampleCount;            // 最大合作次数
    private List<String> codes;                // 折扣码批量搜索列表
    private List<String> emails;               // 邮箱批量精确搜索列表IN 查询
    private List<String> names;                // 姓名批量精确搜索列表IN 查询
    private List<String> blankFields;          // 空白筛选字段列表

    public String getSearchKey() {
        return this.searchKey;
    }

    public Influencer.Stage getStage() {
        return this.stage;
    }

    public Influencer.Status getStatus() {
        return this.status;
    }

    public String getCountry() {
        return this.country;
    }

    public List<String> getPlatform() {
        return this.platform;
    }

    public List<String> getSocialPlatform() {
        return this.socialPlatform;
    }

    public String getBrand() {
        return this.brand;
    }

    public Boolean getIsPaid() {
        return this.isPaid;
    }

    public List<Long> getTagIds() {
        return this.tagIds;
    }

    public List<Long> getLiaisonTagIds() {
        return this.liaisonTagIds;
    }

    public List<Long> getOwnerIds() {
        return this.ownerIds;
    }

    public List<Long> getContactPersonIds() {
        return this.contactPersonIds;
    }

    public String getEmail() {
        return this.email;
    }

    public String getOrigin() {
        return this.origin;
    }

    public List<String> getInfluencerType() {
        return this.influencerType;
    }

    public String getProfileLink() {
        return this.profileLink;
    }

    public String getSocialHandle() {
        return this.socialHandle;
    }

    public List<String> getRace() {
        return this.race;
    }

    public String getTimeType() {
        return this.timeType;
    }

    public String getTimeStart() {
        return this.timeStart;
    }

    public String getTimeEnd() {
        return this.timeEnd;
    }

    public Integer getFanRangeMin() {
        return this.fanRangeMin;
    }

    public Integer getFanRangeMax() {
        return this.fanRangeMax;
    }

    public int getPage() {
        return this.page;
    }

    public int getSize() {
        return this.size;
    }

    public Boolean getHasOutputContent() {
        return this.hasOutputContent;
    }

    public String getDiscountCode() {
        return this.discountCode;
    }

    public Integer getMinSampleCount() {
        return this.minSampleCount;
    }

    public Integer getMaxSampleCount() {
        return this.maxSampleCount;
    }

    public List<String> getCodes() {
        return this.codes;
    }

    public List<String> getEmails() {
        return this.emails;
    }

    public List<String> getNames() {
        return this.names;
    }

    public List<String> getBlankFields() {
        return this.blankFields;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setStage(Influencer.Stage stage) {
        this.stage = stage;
    }

    public void setStatus(Influencer.Status status) {
        this.status = status;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPlatform(List<String> platform) {
        this.platform = platform;
    }

    public void setSocialPlatform(List<String> socialPlatform) {
        this.socialPlatform = socialPlatform;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public void setLiaisonTagIds(List<Long> liaisonTagIds) {
        this.liaisonTagIds = liaisonTagIds;
    }

    public void setOwnerIds(List<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public void setContactPersonIds(List<Long> contactPersonIds) {
        this.contactPersonIds = contactPersonIds;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setInfluencerType(List<String> influencerType) {
        this.influencerType = influencerType;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public void setSocialHandle(String socialHandle) {
        this.socialHandle = socialHandle;
    }

    public void setRace(List<String> race) {
        this.race = race;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setFanRangeMin(Integer fanRangeMin) {
        this.fanRangeMin = fanRangeMin;
    }

    public void setFanRangeMax(Integer fanRangeMax) {
        this.fanRangeMax = fanRangeMax;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setHasOutputContent(Boolean hasOutputContent) {
        this.hasOutputContent = hasOutputContent;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public void setMinSampleCount(Integer minSampleCount) {
        this.minSampleCount = minSampleCount;
    }

    public void setMaxSampleCount(Integer maxSampleCount) {
        this.maxSampleCount = maxSampleCount;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void setBlankFields(List<String> blankFields) {
        this.blankFields = blankFields;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerFilterDto)) {
            return false;
        }
        InfluencerFilterDto other = (InfluencerFilterDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getPage() != other.getPage()) {
            return false;
        }
        if (this.getSize() != other.getSize()) {
            return false;
        }
        Boolean this$isPaid = this.getIsPaid();
        Boolean other$isPaid = other.getIsPaid();
        if (this$isPaid == null ? other$isPaid != null : !((Object)this$isPaid).equals(other$isPaid)) {
            return false;
        }
        List this$ownerIds = this.getOwnerIds();
        List other$ownerIds = other.getOwnerIds();
        if (this$ownerIds == null ? other$ownerIds != null : !((Object)this$ownerIds).equals(other$ownerIds)) {
            return false;
        }
        List this$contactPersonIds = this.getContactPersonIds();
        List other$contactPersonIds = other.getContactPersonIds();
        if (this$contactPersonIds == null ? other$contactPersonIds != null : !((Object)this$contactPersonIds).equals(other$contactPersonIds)) {
            return false;
        }
        Integer this$fanRangeMin = this.getFanRangeMin();
        Integer other$fanRangeMin = other.getFanRangeMin();
        if (this$fanRangeMin == null ? other$fanRangeMin != null : !((Object)this$fanRangeMin).equals(other$fanRangeMin)) {
            return false;
        }
        Integer this$fanRangeMax = this.getFanRangeMax();
        Integer other$fanRangeMax = other.getFanRangeMax();
        if (this$fanRangeMax == null ? other$fanRangeMax != null : !((Object)this$fanRangeMax).equals(other$fanRangeMax)) {
            return false;
        }
        Boolean this$hasOutputContent = this.getHasOutputContent();
        Boolean other$hasOutputContent = other.getHasOutputContent();
        if (this$hasOutputContent == null ? other$hasOutputContent != null : !((Object)this$hasOutputContent).equals(other$hasOutputContent)) {
            return false;
        }
        Integer this$minSampleCount = this.getMinSampleCount();
        Integer other$minSampleCount = other.getMinSampleCount();
        if (this$minSampleCount == null ? other$minSampleCount != null : !((Object)this$minSampleCount).equals(other$minSampleCount)) {
            return false;
        }
        Integer this$maxSampleCount = this.getMaxSampleCount();
        Integer other$maxSampleCount = other.getMaxSampleCount();
        if (this$maxSampleCount == null ? other$maxSampleCount != null : !((Object)this$maxSampleCount).equals(other$maxSampleCount)) {
            return false;
        }
        String this$searchKey = this.getSearchKey();
        String other$searchKey = other.getSearchKey();
        if (this$searchKey == null ? other$searchKey != null : !this$searchKey.equals(other$searchKey)) {
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
        String this$country = this.getCountry();
        String other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
            return false;
        }
        List this$platform = this.getPlatform();
        List other$platform = other.getPlatform();
        if (this$platform == null ? other$platform != null : !((Object)this$platform).equals(other$platform)) {
            return false;
        }
        List this$socialPlatform = this.getSocialPlatform();
        List other$socialPlatform = other.getSocialPlatform();
        if (this$socialPlatform == null ? other$socialPlatform != null : !((Object)this$socialPlatform).equals(other$socialPlatform)) {
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
        List this$liaisonTagIds = this.getLiaisonTagIds();
        List other$liaisonTagIds = other.getLiaisonTagIds();
        if (this$liaisonTagIds == null ? other$liaisonTagIds != null : !((Object)this$liaisonTagIds).equals(other$liaisonTagIds)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$origin = this.getOrigin();
        String other$origin = other.getOrigin();
        if (this$origin == null ? other$origin != null : !this$origin.equals(other$origin)) {
            return false;
        }
        List this$influencerType = this.getInfluencerType();
        List other$influencerType = other.getInfluencerType();
        if (this$influencerType == null ? other$influencerType != null : !((Object)this$influencerType).equals(other$influencerType)) {
            return false;
        }
        String this$profileLink = this.getProfileLink();
        String other$profileLink = other.getProfileLink();
        if (this$profileLink == null ? other$profileLink != null : !this$profileLink.equals(other$profileLink)) {
            return false;
        }
        String this$socialHandle = this.getSocialHandle();
        String other$socialHandle = other.getSocialHandle();
        if (this$socialHandle == null ? other$socialHandle != null : !this$socialHandle.equals(other$socialHandle)) {
            return false;
        }
        List this$race = this.getRace();
        List other$race = other.getRace();
        if (this$race == null ? other$race != null : !((Object)this$race).equals(other$race)) {
            return false;
        }
        String this$timeType = this.getTimeType();
        String other$timeType = other.getTimeType();
        if (this$timeType == null ? other$timeType != null : !this$timeType.equals(other$timeType)) {
            return false;
        }
        String this$timeStart = this.getTimeStart();
        String other$timeStart = other.getTimeStart();
        if (this$timeStart == null ? other$timeStart != null : !this$timeStart.equals(other$timeStart)) {
            return false;
        }
        String this$timeEnd = this.getTimeEnd();
        String other$timeEnd = other.getTimeEnd();
        if (this$timeEnd == null ? other$timeEnd != null : !this$timeEnd.equals(other$timeEnd)) {
            return false;
        }
        String this$discountCode = this.getDiscountCode();
        String other$discountCode = other.getDiscountCode();
        if (this$discountCode == null ? other$discountCode != null : !this$discountCode.equals(other$discountCode)) {
            return false;
        }
        List this$codes = this.getCodes();
        List other$codes = other.getCodes();
        if (this$codes == null ? other$codes != null : !((Object)this$codes).equals(other$codes)) {
            return false;
        }
        List this$emails = this.getEmails();
        List other$emails = other.getEmails();
        return !(this$emails == null ? other$emails != null : !((Object)this$emails).equals(other$emails));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerFilterDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPage();
        result = result * 59 + this.getSize();
        Boolean $isPaid = this.getIsPaid();
        result = result * 59 + ($isPaid == null ? 43 : ((Object)$isPaid).hashCode());
        List $ownerIds = this.getOwnerIds();
        result = result * 59 + ($ownerIds == null ? 43 : ((Object)$ownerIds).hashCode());
        List $contactPersonIds = this.getContactPersonIds();
        result = result * 59 + ($contactPersonIds == null ? 43 : ((Object)$contactPersonIds).hashCode());
        Integer $fanRangeMin = this.getFanRangeMin();
        result = result * 59 + ($fanRangeMin == null ? 43 : ((Object)$fanRangeMin).hashCode());
        Integer $fanRangeMax = this.getFanRangeMax();
        result = result * 59 + ($fanRangeMax == null ? 43 : ((Object)$fanRangeMax).hashCode());
        Boolean $hasOutputContent = this.getHasOutputContent();
        result = result * 59 + ($hasOutputContent == null ? 43 : ((Object)$hasOutputContent).hashCode());
        Integer $minSampleCount = this.getMinSampleCount();
        result = result * 59 + ($minSampleCount == null ? 43 : ((Object)$minSampleCount).hashCode());
        Integer $maxSampleCount = this.getMaxSampleCount();
        result = result * 59 + ($maxSampleCount == null ? 43 : ((Object)$maxSampleCount).hashCode());
        String $searchKey = this.getSearchKey();
        result = result * 59 + ($searchKey == null ? 43 : $searchKey.hashCode());
        Influencer.Stage $stage = this.getStage();
        result = result * 59 + ($stage == null ? 43 : $stage.hashCode());
        Influencer.Status $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        List $platform = this.getPlatform();
        result = result * 59 + ($platform == null ? 43 : ((Object)$platform).hashCode());
        List $socialPlatform = this.getSocialPlatform();
        result = result * 59 + ($socialPlatform == null ? 43 : ((Object)$socialPlatform).hashCode());
        String $brand = this.getBrand();
        result = result * 59 + ($brand == null ? 43 : $brand.hashCode());
        List $tagIds = this.getTagIds();
        result = result * 59 + ($tagIds == null ? 43 : ((Object)$tagIds).hashCode());
        List $liaisonTagIds = this.getLiaisonTagIds();
        result = result * 59 + ($liaisonTagIds == null ? 43 : ((Object)$liaisonTagIds).hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $origin = this.getOrigin();
        result = result * 59 + ($origin == null ? 43 : $origin.hashCode());
        List $influencerType = this.getInfluencerType();
        result = result * 59 + ($influencerType == null ? 43 : ((Object)$influencerType).hashCode());
        String $profileLink = this.getProfileLink();
        result = result * 59 + ($profileLink == null ? 43 : $profileLink.hashCode());
        String $socialHandle = this.getSocialHandle();
        result = result * 59 + ($socialHandle == null ? 43 : $socialHandle.hashCode());
        List $race = this.getRace();
        result = result * 59 + ($race == null ? 43 : ((Object)$race).hashCode());
        String $timeType = this.getTimeType();
        result = result * 59 + ($timeType == null ? 43 : $timeType.hashCode());
        String $timeStart = this.getTimeStart();
        result = result * 59 + ($timeStart == null ? 43 : $timeStart.hashCode());
        String $timeEnd = this.getTimeEnd();
        result = result * 59 + ($timeEnd == null ? 43 : $timeEnd.hashCode());
        String $discountCode = this.getDiscountCode();
        result = result * 59 + ($discountCode == null ? 43 : $discountCode.hashCode());
        List $codes = this.getCodes();
        result = result * 59 + ($codes == null ? 43 : ((Object)$codes).hashCode());
        List $emails = this.getEmails();
        result = result * 59 + ($emails == null ? 43 : ((Object)$emails).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerFilterDto(searchKey=" + this.getSearchKey() + ", stage=" + this.getStage() + ", status=" + this.getStatus() + ", country=" + this.getCountry() + ", platform=" + this.getPlatform() + ", socialPlatform=" + this.getSocialPlatform() + ", brand=" + this.getBrand() + ", isPaid=" + this.getIsPaid() + ", tagIds=" + this.getTagIds() + ", liaisonTagIds=" + this.getLiaisonTagIds() + ", ownerIds=" + this.getOwnerIds() + ", contactPersonIds=" + this.getContactPersonIds() + ", email=" + this.getEmail() + ", origin=" + this.getOrigin() + ", influencerType=" + this.getInfluencerType() + ", profileLink=" + this.getProfileLink() + ", socialHandle=" + this.getSocialHandle() + ", race=" + this.getRace() + ", timeType=" + this.getTimeType() + ", timeStart=" + this.getTimeStart() + ", timeEnd=" + this.getTimeEnd() + ", fanRangeMin=" + this.getFanRangeMin() + ", fanRangeMax=" + this.getFanRangeMax() + ", page=" + this.getPage() + ", size=" + this.getSize() + ", hasOutputContent=" + this.getHasOutputContent() + ", discountCode=" + this.getDiscountCode() + ", minSampleCount=" + this.getMinSampleCount() + ", maxSampleCount=" + this.getMaxSampleCount() + ", codes=" + this.getCodes() + ", emails=" + this.getEmails() + ")";
    }
}

