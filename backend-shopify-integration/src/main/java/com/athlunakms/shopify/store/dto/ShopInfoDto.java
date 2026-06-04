package com.athlunakms.shopify.store.dto;

import com.athlunakms.shopify.store.dto.ShopInfoDto;

public class ShopInfoDto {
    private Long id;
    private String name;
    private String domain;
    private String email;
    private String plan;
    private String currency;
    private String timezone;
    private String country;
    private String countryCode;
    private String phone;
    private String createdAt;

    public static ShopInfoDtoBuilder builder() {
        return new ShopInfoDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPlan() {
        return this.plan;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public String getCountry() {
        return this.country;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopInfoDto)) {
            return false;
        }
        ShopInfoDto other = (ShopInfoDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$domain = this.getDomain();
        String other$domain = other.getDomain();
        if (this$domain == null ? other$domain != null : !this$domain.equals(other$domain)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$plan = this.getPlan();
        String other$plan = other.getPlan();
        if (this$plan == null ? other$plan != null : !this$plan.equals(other$plan)) {
            return false;
        }
        String this$currency = this.getCurrency();
        String other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) {
            return false;
        }
        String this$timezone = this.getTimezone();
        String other$timezone = other.getTimezone();
        if (this$timezone == null ? other$timezone != null : !this$timezone.equals(other$timezone)) {
            return false;
        }
        String this$country = this.getCountry();
        String other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
            return false;
        }
        String this$countryCode = this.getCountryCode();
        String other$countryCode = other.getCountryCode();
        if (this$countryCode == null ? other$countryCode != null : !this$countryCode.equals(other$countryCode)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        String this$createdAt = this.getCreatedAt();
        String other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopInfoDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $domain = this.getDomain();
        result = result * 59 + ($domain == null ? 43 : $domain.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $plan = this.getPlan();
        result = result * 59 + ($plan == null ? 43 : $plan.hashCode());
        String $currency = this.getCurrency();
        result = result * 59 + ($currency == null ? 43 : $currency.hashCode());
        String $timezone = this.getTimezone();
        result = result * 59 + ($timezone == null ? 43 : $timezone.hashCode());
        String $country = this.getCountry();
        result = result * 59 + ($country == null ? 43 : $country.hashCode());
        String $countryCode = this.getCountryCode();
        result = result * 59 + ($countryCode == null ? 43 : $countryCode.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        String $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : $createdAt.hashCode());
        return result;
    }

    public String toString() {
        return "ShopInfoDto(id=" + this.getId() + ", name=" + this.getName() + ", domain=" + this.getDomain() + ", email=" + this.getEmail() + ", plan=" + this.getPlan() + ", currency=" + this.getCurrency() + ", timezone=" + this.getTimezone() + ", country=" + this.getCountry() + ", countryCode=" + this.getCountryCode() + ", phone=" + this.getPhone() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    public ShopInfoDto() {
    }

    public ShopInfoDto(Long id, String name, String domain, String email, String plan, String currency, String timezone, String country, String countryCode, String phone, String createdAt) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.email = email;
        this.plan = plan;
        this.currency = currency;
        this.timezone = timezone;
        this.country = country;
        this.countryCode = countryCode;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public static class ShopInfoDtoBuilder {
        private Long id;
        private String name;
        private String domain;
        private String email;
        private String plan;
        private String currency;
        private String timezone;
        private String country;
        private String countryCode;
        private String phone;
        private String createdAt;

        ShopInfoDtoBuilder() {
        }

        public ShopInfoDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ShopInfoDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ShopInfoDtoBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public ShopInfoDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ShopInfoDtoBuilder plan(String plan) {
            this.plan = plan;
            return this;
        }

        public ShopInfoDtoBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public ShopInfoDtoBuilder timezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public ShopInfoDtoBuilder country(String country) {
            this.country = country;
            return this;
        }

        public ShopInfoDtoBuilder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public ShopInfoDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public ShopInfoDtoBuilder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ShopInfoDto build() {
            return new ShopInfoDto(this.id, this.name, this.domain, this.email, this.plan, this.currency, this.timezone, this.country, this.countryCode, this.phone, this.createdAt);
        }

        public String toString() {
            return "ShopInfoDto.ShopInfoDtoBuilder(id=" + this.id + ", name=" + this.name + ", domain=" + this.domain + ", email=" + this.email + ", plan=" + this.plan + ", currency=" + this.currency + ", timezone=" + this.timezone + ", country=" + this.country + ", countryCode=" + this.countryCode + ", phone=" + this.phone + ", createdAt=" + this.createdAt + ")";
        }
    }
}

