package com.athlunakms.influencer.dto;

public class InfluencerImportDto {
    private String realName;
    private String email;
    private String picName;
    private Long ownerId;
    private Long contactPersonId;
    private String igLink;
    private String ttLink;
    private String ytLink;
    private String followersRaw;
    private String kolTier;
    private String category;
    private String style;
    private String project;
    private String contentRequired;
    private String costRaw;
    private String country;
    private String address;
    private String phone;
    private String promoCode;
    private String paypalAccount;
    private String bankName;
    private String branchName;
    private String bankAddress;
    private String swiftCode;
    private String routingNumber;
    private String accountName;
    private String accountNumber;
    private String beneficiaryAddress;
    private String orderNos;

    public String getRealName() {
        return this.realName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPicName() {
        return this.picName;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Long getContactPersonId() {
        return this.contactPersonId;
    }

    public String getIgLink() {
        return this.igLink;
    }

    public String getTtLink() {
        return this.ttLink;
    }

    public String getYtLink() {
        return this.ytLink;
    }

    public String getFollowersRaw() {
        return this.followersRaw;
    }

    public String getKolTier() {
        return this.kolTier;
    }

    public String getCategory() {
        return this.category;
    }

    public String getStyle() {
        return this.style;
    }

    public String getProject() {
        return this.project;
    }

    public String getContentRequired() {
        return this.contentRequired;
    }

    public String getCostRaw() {
        return this.costRaw;
    }

    public String getCountry() {
        return this.country;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPromoCode() {
        return this.promoCode;
    }

    public String getPaypalAccount() {
        return this.paypalAccount;
    }

    public String getBankName() {
        return this.bankName;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public String getBankAddress() {
        return this.bankAddress;
    }

    public String getSwiftCode() {
        return this.swiftCode;
    }

    public String getRoutingNumber() {
        return this.routingNumber;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getBeneficiaryAddress() {
        return this.beneficiaryAddress;
    }

    public String getOrderNos() {
        return this.orderNos;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public void setIgLink(String igLink) {
        this.igLink = igLink;
    }

    public void setTtLink(String ttLink) {
        this.ttLink = ttLink;
    }

    public void setYtLink(String ytLink) {
        this.ytLink = ytLink;
    }

    public void setFollowersRaw(String followersRaw) {
        this.followersRaw = followersRaw;
    }

    public void setKolTier(String kolTier) {
        this.kolTier = kolTier;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setContentRequired(String contentRequired) {
        this.contentRequired = contentRequired;
    }

    public void setCostRaw(String costRaw) {
        this.costRaw = costRaw;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public void setPaypalAccount(String paypalAccount) {
        this.paypalAccount = paypalAccount;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBeneficiaryAddress(String beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress;
    }

    public void setOrderNos(String orderNos) {
        this.orderNos = orderNos;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerImportDto)) {
            return false;
        }
        InfluencerImportDto other = (InfluencerImportDto)o;
        if (!other.canEqual((Object)this)) {
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
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$picName = this.getPicName();
        String other$picName = other.getPicName();
        if (this$picName == null ? other$picName != null : !this$picName.equals(other$picName)) {
            return false;
        }
        String this$igLink = this.getIgLink();
        String other$igLink = other.getIgLink();
        if (this$igLink == null ? other$igLink != null : !this$igLink.equals(other$igLink)) {
            return false;
        }
        String this$ttLink = this.getTtLink();
        String other$ttLink = other.getTtLink();
        if (this$ttLink == null ? other$ttLink != null : !this$ttLink.equals(other$ttLink)) {
            return false;
        }
        String this$ytLink = this.getYtLink();
        String other$ytLink = other.getYtLink();
        if (this$ytLink == null ? other$ytLink != null : !this$ytLink.equals(other$ytLink)) {
            return false;
        }
        String this$followersRaw = this.getFollowersRaw();
        String other$followersRaw = other.getFollowersRaw();
        if (this$followersRaw == null ? other$followersRaw != null : !this$followersRaw.equals(other$followersRaw)) {
            return false;
        }
        String this$kolTier = this.getKolTier();
        String other$kolTier = other.getKolTier();
        if (this$kolTier == null ? other$kolTier != null : !this$kolTier.equals(other$kolTier)) {
            return false;
        }
        String this$category = this.getCategory();
        String other$category = other.getCategory();
        if (this$category == null ? other$category != null : !this$category.equals(other$category)) {
            return false;
        }
        String this$style = this.getStyle();
        String other$style = other.getStyle();
        if (this$style == null ? other$style != null : !this$style.equals(other$style)) {
            return false;
        }
        String this$project = this.getProject();
        String other$project = other.getProject();
        if (this$project == null ? other$project != null : !this$project.equals(other$project)) {
            return false;
        }
        String this$contentRequired = this.getContentRequired();
        String other$contentRequired = other.getContentRequired();
        if (this$contentRequired == null ? other$contentRequired != null : !this$contentRequired.equals(other$contentRequired)) {
            return false;
        }
        String this$costRaw = this.getCostRaw();
        String other$costRaw = other.getCostRaw();
        if (this$costRaw == null ? other$costRaw != null : !this$costRaw.equals(other$costRaw)) {
            return false;
        }
        String this$country = this.getCountry();
        String other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
            return false;
        }
        String this$address = this.getAddress();
        String other$address = other.getAddress();
        if (this$address == null ? other$address != null : !this$address.equals(other$address)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        String this$promoCode = this.getPromoCode();
        String other$promoCode = other.getPromoCode();
        if (this$promoCode == null ? other$promoCode != null : !this$promoCode.equals(other$promoCode)) {
            return false;
        }
        String this$paypalAccount = this.getPaypalAccount();
        String other$paypalAccount = other.getPaypalAccount();
        if (this$paypalAccount == null ? other$paypalAccount != null : !this$paypalAccount.equals(other$paypalAccount)) {
            return false;
        }
        String this$bankName = this.getBankName();
        String other$bankName = other.getBankName();
        if (this$bankName == null ? other$bankName != null : !this$bankName.equals(other$bankName)) {
            return false;
        }
        String this$branchName = this.getBranchName();
        String other$branchName = other.getBranchName();
        if (this$branchName == null ? other$branchName != null : !this$branchName.equals(other$branchName)) {
            return false;
        }
        String this$bankAddress = this.getBankAddress();
        String other$bankAddress = other.getBankAddress();
        if (this$bankAddress == null ? other$bankAddress != null : !this$bankAddress.equals(other$bankAddress)) {
            return false;
        }
        String this$swiftCode = this.getSwiftCode();
        String other$swiftCode = other.getSwiftCode();
        if (this$swiftCode == null ? other$swiftCode != null : !this$swiftCode.equals(other$swiftCode)) {
            return false;
        }
        String this$routingNumber = this.getRoutingNumber();
        String other$routingNumber = other.getRoutingNumber();
        if (this$routingNumber == null ? other$routingNumber != null : !this$routingNumber.equals(other$routingNumber)) {
            return false;
        }
        String this$accountName = this.getAccountName();
        String other$accountName = other.getAccountName();
        if (this$accountName == null ? other$accountName != null : !this$accountName.equals(other$accountName)) {
            return false;
        }
        String this$accountNumber = this.getAccountNumber();
        String other$accountNumber = other.getAccountNumber();
        if (this$accountNumber == null ? other$accountNumber != null : !this$accountNumber.equals(other$accountNumber)) {
            return false;
        }
        String this$beneficiaryAddress = this.getBeneficiaryAddress();
        String other$beneficiaryAddress = other.getBeneficiaryAddress();
        if (this$beneficiaryAddress == null ? other$beneficiaryAddress != null : !this$beneficiaryAddress.equals(other$beneficiaryAddress)) {
            return false;
        }
        String this$orderNos = this.getOrderNos();
        String other$orderNos = other.getOrderNos();
        return !(this$orderNos == null ? other$orderNos != null : !this$orderNos.equals(other$orderNos));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerImportDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $ownerId = this.getOwnerId();
        result = result * 59 + ($ownerId == null ? 43 : ((Object)$ownerId).hashCode());
        Long $contactPersonId = this.getContactPersonId();
        result = result * 59 + ($contactPersonId == null ? 43 : ((Object)$contactPersonId).hashCode());
        String $realName = this.getRealName();
        result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $picName = this.getPicName();
        result = result * 59 + ($picName == null ? 43 : $picName.hashCode());
        String $igLink = this.getIgLink();
        result = result * 59 + ($igLink == null ? 43 : $igLink.hashCode());
        String $ttLink = this.getTtLink();
        result = result * 59 + ($ttLink == null ? 43 : $ttLink.hashCode());
        String $ytLink = this.getYtLink();
        result = result * 59 + ($ytLink == null ? 43 : $ytLink.hashCode());
        String $followersRaw = this.getFollowersRaw();
        result = result * 59 + ($followersRaw == null ? 43 : $followersRaw.hashCode());
        String $kolTier = this.getKolTier();
        result = result * 59 + ($kolTier == null ? 43 : $kolTier.hashCode());
        String $category = this.getCategory();
        result = result * 59 + ($category == null ? 43 : $category.hashCode());
        String $style = this.getStyle();
        result = result * 59 + ($style == null ? 43 : $style.hashCode());
        String $project = this.getProject();
        result = result * 59 + ($project == null ? 43 : $project.hashCode());
        String $contentRequired = this.getContentRequired();
        result = result * 59 + ($contentRequired == null ? 43 : $contentRequired.hashCode());
        String $costRaw = this.getCostRaw();
        result = result * 59 + ($costRaw == null ? 43 : $costRaw.hashCode());
        String $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        String $address = this.getAddress();
        result = result * 59 + ($address == null ? 43 : $address.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $promoCode = this.getPromoCode();
        result = result * 59 + ($promoCode == null ? 43 : $promoCode.hashCode());
        String $paypalAccount = this.getPaypalAccount();
        result = result * 59 + ($paypalAccount == null ? 43 : $paypalAccount.hashCode());
        String $bankName = this.getBankName();
        result = result * 59 + ($bankName == null ? 43 : $bankName.hashCode());
        String $branchName = this.getBranchName();
        result = result * 59 + ($branchName == null ? 43 : $branchName.hashCode());
        String $bankAddress = this.getBankAddress();
        result = result * 59 + ($bankAddress == null ? 43 : $bankAddress.hashCode());
        String $swiftCode = this.getSwiftCode();
        result = result * 59 + ($swiftCode == null ? 43 : $swiftCode.hashCode());
        String $routingNumber = this.getRoutingNumber();
        result = result * 59 + ($routingNumber == null ? 43 : $routingNumber.hashCode());
        String $accountName = this.getAccountName();
        result = result * 59 + ($accountName == null ? 43 : $accountName.hashCode());
        String $accountNumber = this.getAccountNumber();
        result = result * 59 + ($accountNumber == null ? 43 : $accountNumber.hashCode());
        String $beneficiaryAddress = this.getBeneficiaryAddress();
        result = result * 59 + ($beneficiaryAddress == null ? 43 : $beneficiaryAddress.hashCode());
        String $orderNos = this.getOrderNos();
        result = result * 59 + ($orderNos == null ? 43 : $orderNos.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerImportDto(realName=" + this.getRealName() + ", email=" + this.getEmail() + ", picName=" + this.getPicName() + ", ownerId=" + this.getOwnerId() + ", contactPersonId=" + this.getContactPersonId() + ", igLink=" + this.getIgLink() + ", ttLink=" + this.getTtLink() + ", ytLink=" + this.getYtLink() + ", followersRaw=" + this.getFollowersRaw() + ", kolTier=" + this.getKolTier() + ", category=" + this.getCategory() + ", style=" + this.getStyle() + ", project=" + this.getProject() + ", contentRequired=" + this.getContentRequired() + ", costRaw=" + this.getCostRaw() + ", country=" + this.getCountry() + ", address=" + this.getAddress() + ", phone=" + this.getPhone() + ", promoCode=" + this.getPromoCode() + ", paypalAccount=" + this.getPaypalAccount() + ", bankName=" + this.getBankName() + ", branchName=" + this.getBranchName() + ", bankAddress=" + this.getBankAddress() + ", swiftCode=" + this.getSwiftCode() + ", routingNumber=" + this.getRoutingNumber() + ", accountName=" + this.getAccountName() + ", accountNumber=" + this.getAccountNumber() + ", beneficiaryAddress=" + this.getBeneficiaryAddress() + ", orderNos=" + this.getOrderNos() + ")";
    }
}

