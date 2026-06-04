package com.athlunakms.user.dto;

public class CountryDto {
    private Integer id;
    private String code;
    private String nameCn;
    private String nameEn;
    private String phonePrefix;
    private String timezone;
    private Integer sortOrder;
    private Boolean enabled;

    public Integer getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getNameCn() {
        return this.nameCn;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public String getPhonePrefix() {
        return this.phonePrefix;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CountryDto)) {
            return false;
        }
        CountryDto other = (CountryDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$id = this.getId();
        Integer other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$sortOrder = this.getSortOrder();
        Integer other$sortOrder = other.getSortOrder();
        if (this$sortOrder == null ? other$sortOrder != null : !((Object)this$sortOrder).equals(other$sortOrder)) {
            return false;
        }
        Boolean this$enabled = this.getEnabled();
        Boolean other$enabled = other.getEnabled();
        if (this$enabled == null ? other$enabled != null : !((Object)this$enabled).equals(other$enabled)) {
            return false;
        }
        String this$code = this.getCode();
        String other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) {
            return false;
        }
        String this$nameCn = this.getNameCn();
        String other$nameCn = other.getNameCn();
        if (this$nameCn == null ? other$nameCn != null : !this$nameCn.equals(other$nameCn)) {
            return false;
        }
        String this$nameEn = this.getNameEn();
        String other$nameEn = other.getNameEn();
        if (this$nameEn == null ? other$nameEn != null : !this$nameEn.equals(other$nameEn)) {
            return false;
        }
        String this$phonePrefix = this.getPhonePrefix();
        String other$phonePrefix = other.getPhonePrefix();
        if (this$phonePrefix == null ? other$phonePrefix != null : !this$phonePrefix.equals(other$phonePrefix)) {
            return false;
        }
        String this$timezone = this.getTimezone();
        String other$timezone = other.getTimezone();
        return !(this$timezone == null ? other$timezone != null : !this$timezone.equals(other$timezone));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CountryDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $sortOrder = this.getSortOrder();
        result = result * 59 + ($sortOrder == null ? 43 : ((Object)$sortOrder).hashCode());
        Boolean $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : ((Object)$enabled).hashCode());
        String $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        String $nameCn = this.getNameCn();
        result = result * 59 + ($nameCn == null ? 43 : $nameCn.hashCode());
        String $nameEn = this.getNameEn();
        result = result * 59 + ($nameEn == null ? 43 : $nameEn.hashCode());
        String $phonePrefix = this.getPhonePrefix();
        result = result * 59 + ($phonePrefix == null ? 43 : $phonePrefix.hashCode());
        String $timezone = this.getTimezone();
        result = result * 59 + ($timezone == null ? 43 : $timezone.hashCode());
        return result;
    }

    public String toString() {
        return "CountryDto(id=" + this.getId() + ", code=" + this.getCode() + ", nameCn=" + this.getNameCn() + ", nameEn=" + this.getNameEn() + ", phonePrefix=" + this.getPhonePrefix() + ", timezone=" + this.getTimezone() + ", sortOrder=" + this.getSortOrder() + ", enabled=" + this.getEnabled() + ")";
    }
}

