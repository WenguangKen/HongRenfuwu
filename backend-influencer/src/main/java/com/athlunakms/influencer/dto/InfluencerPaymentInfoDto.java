package com.athlunakms.influencer.dto;

public class InfluencerPaymentInfoDto {
    private Long id;
    private Long influencerId;
    private String bankCountry;
    private String bankName;
    private String branchName;
    private String bankAddress;
    private String swiftCode;
    private String routingNumber;
    private String accountName;
    private String accountNumber;
    private String beneficiaryAddress;
    private String paypalAccount;

    public Long getId() {
        return this.id;
    }

    public Long getInfluencerId() {
        return this.influencerId;
    }

    public String getBankCountry() {
        return this.bankCountry;
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

    public String getPaypalAccount() {
        return this.paypalAccount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfluencerId(Long influencerId) {
        this.influencerId = influencerId;
    }

    public void setBankCountry(String bankCountry) {
        this.bankCountry = bankCountry;
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

    public void setPaypalAccount(String paypalAccount) {
        this.paypalAccount = paypalAccount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerPaymentInfoDto)) {
            return false;
        }
        InfluencerPaymentInfoDto other = (InfluencerPaymentInfoDto)o;
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
        String this$bankCountry = this.getBankCountry();
        String other$bankCountry = other.getBankCountry();
        if (this$bankCountry == null ? other$bankCountry != null : !this$bankCountry.equals(other$bankCountry)) {
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
        String this$paypalAccount = this.getPaypalAccount();
        String other$paypalAccount = other.getPaypalAccount();
        return !(this$paypalAccount == null ? other$paypalAccount != null : !this$paypalAccount.equals(other$paypalAccount));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerPaymentInfoDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $influencerId = this.getInfluencerId();
        result = result * 59 + ($influencerId == null ? 43 : ((Object)$influencerId).hashCode());
        String $bankCountry = this.getBankCountry();
        result = result * 59 + ($bankCountry == null ? 43 : $bankCountry.hashCode());
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
        String $paypalAccount = this.getPaypalAccount();
        result = result * 59 + ($paypalAccount == null ? 43 : $paypalAccount.hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerPaymentInfoDto(id=" + this.getId() + ", influencerId=" + this.getInfluencerId() + ", bankCountry=" + this.getBankCountry() + ", bankName=" + this.getBankName() + ", branchName=" + this.getBranchName() + ", bankAddress=" + this.getBankAddress() + ", swiftCode=" + this.getSwiftCode() + ", routingNumber=" + this.getRoutingNumber() + ", accountName=" + this.getAccountName() + ", accountNumber=" + this.getAccountNumber() + ", beneficiaryAddress=" + this.getBeneficiaryAddress() + ", paypalAccount=" + this.getPaypalAccount() + ")";
    }
}

