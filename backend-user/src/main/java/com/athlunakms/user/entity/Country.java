package com.athlunakms.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="countries")
public class Country {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @Column(name="code", nullable=false, unique=true, length=3)
    private String code;
    @Column(name="name_cn", nullable=false, length=100)
    private String nameCn;
    @Column(name="name_en", length=100)
    private String nameEn;
    @Column(name="phone_prefix", length=20)
    private String phonePrefix;
    @Column(name="timezone", length=20)
    private String timezone;
    @Column(name="sort_order")
    private Integer sortOrder = 0;
    @Column(name="enabled")
    private Boolean enabled = true;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        Country other = (Country)o;
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
        if (this$timezone == null ? other$timezone != null : !this$timezone.equals(other$timezone)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        return !(this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Country;
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
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "Country(id=" + this.getId() + ", code=" + this.getCode() + ", nameCn=" + this.getNameCn() + ", nameEn=" + this.getNameEn() + ", phonePrefix=" + this.getPhonePrefix() + ", timezone=" + this.getTimezone() + ", sortOrder=" + this.getSortOrder() + ", enabled=" + this.getEnabled() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

