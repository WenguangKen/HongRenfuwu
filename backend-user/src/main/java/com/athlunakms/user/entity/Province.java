package com.athlunakms.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="provinces")
public class Province {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="store_id")
    private Long storeId;
    @Column(name="country_id")
    private Long countryId;
    @Column(name="country_code", nullable=false)
    private String countryCode;
    @Column(name="country_name")
    private String countryName;
    @Column(name="province_id")
    private Long provinceId;
    @Column(name="province_code", nullable=false)
    private String provinceCode;
    @Column(name="province_name", nullable=false)
    private String provinceName;
    private BigDecimal tax;
    @Column(name="tax_name")
    private String taxName;
    @Column(name="tax_percentage")
    private BigDecimal taxPercentage;
    private Boolean enabled = true;
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public String getProvinceCode() {
        return this.provinceCode;
    }

    public String getProvinceName() {
        return this.provinceName;
    }

    public BigDecimal getTax() {
        return this.tax;
    }

    public String getTaxName() {
        return this.taxName;
    }

    public BigDecimal getTaxPercentage() {
        return this.taxPercentage;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
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
        if (!(o instanceof Province)) {
            return false;
        }
        Province other = (Province)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$storeId = this.getStoreId();
        Long other$storeId = other.getStoreId();
        if (this$storeId == null ? other$storeId != null : !((Object)this$storeId).equals(other$storeId)) {
            return false;
        }
        Long this$countryId = this.getCountryId();
        Long other$countryId = other.getCountryId();
        if (this$countryId == null ? other$countryId != null : !((Object)this$countryId).equals(other$countryId)) {
            return false;
        }
        Long this$provinceId = this.getProvinceId();
        Long other$provinceId = other.getProvinceId();
        if (this$provinceId == null ? other$provinceId != null : !((Object)this$provinceId).equals(other$provinceId)) {
            return false;
        }
        Boolean this$enabled = this.getEnabled();
        Boolean other$enabled = other.getEnabled();
        if (this$enabled == null ? other$enabled != null : !((Object)this$enabled).equals(other$enabled)) {
            return false;
        }
        String this$countryCode = this.getCountryCode();
        String other$countryCode = other.getCountryCode();
        if (this$countryCode == null ? other$countryCode != null : !this$countryCode.equals(other$countryCode)) {
            return false;
        }
        String this$countryName = this.getCountryName();
        String other$countryName = other.getCountryName();
        if (this$countryName == null ? other$countryName != null : !this$countryName.equals(other$countryName)) {
            return false;
        }
        String this$provinceCode = this.getProvinceCode();
        String other$provinceCode = other.getProvinceCode();
        if (this$provinceCode == null ? other$provinceCode != null : !this$provinceCode.equals(other$provinceCode)) {
            return false;
        }
        String this$provinceName = this.getProvinceName();
        String other$provinceName = other.getProvinceName();
        if (this$provinceName == null ? other$provinceName != null : !this$provinceName.equals(other$provinceName)) {
            return false;
        }
        BigDecimal this$tax = this.getTax();
        BigDecimal other$tax = other.getTax();
        if (this$tax == null ? other$tax != null : !((Object)this$tax).equals(other$tax)) {
            return false;
        }
        String this$taxName = this.getTaxName();
        String other$taxName = other.getTaxName();
        if (this$taxName == null ? other$taxName != null : !this$taxName.equals(other$taxName)) {
            return false;
        }
        BigDecimal this$taxPercentage = this.getTaxPercentage();
        BigDecimal other$taxPercentage = other.getTaxPercentage();
        if (this$taxPercentage == null ? other$taxPercentage != null : !((Object)this$taxPercentage).equals(other$taxPercentage)) {
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
        return other instanceof Province;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $storeId = this.getStoreId();
        result = result * 59 + ($storeId == null ? 43 : ((Object)$storeId).hashCode());
        Long $countryId = this.getCountryId();
        result = result * 59 + ($countryId == null ? 43 : ((Object)$countryId).hashCode());
        Long $provinceId = this.getProvinceId();
        result = result * 59 + ($provinceId == null ? 43 : ((Object)$provinceId).hashCode());
        Boolean $enabled = this.getEnabled();
        result = result * 59 + ($enabled == null ? 43 : ((Object)$enabled).hashCode());
        String $countryCode = this.getCountryCode();
        result = result * 59 + ($countryCode == null ? 43 : $countryCode.hashCode());
        String $countryName = this.getCountryName();
        result = result * 59 + ($countryName == null ? 43 : $countryName.hashCode());
        String $provinceCode = this.getProvinceCode();
        result = result * 59 + ($provinceCode == null ? 43 : $provinceCode.hashCode());
        String $provinceName = this.getProvinceName();
        result = result * 59 + ($provinceName == null ? 43 : $provinceName.hashCode());
        BigDecimal $tax = this.getTax();
        result = result * 59 + ($tax == null ? 43 : ((Object)$tax).hashCode());
        String $taxName = this.getTaxName();
        result = result * 59 + ($taxName == null ? 43 : $taxName.hashCode());
        BigDecimal $taxPercentage = this.getTaxPercentage();
        result = result * 59 + ($taxPercentage == null ? 43 : ((Object)$taxPercentage).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "Province(id=" + this.getId() + ", storeId=" + this.getStoreId() + ", countryId=" + this.getCountryId() + ", countryCode=" + this.getCountryCode() + ", countryName=" + this.getCountryName() + ", provinceId=" + this.getProvinceId() + ", provinceCode=" + this.getProvinceCode() + ", provinceName=" + this.getProvinceName() + ", tax=" + this.getTax() + ", taxName=" + this.getTaxName() + ", taxPercentage=" + this.getTaxPercentage() + ", enabled=" + this.getEnabled() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

