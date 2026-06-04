package com.athlunakms.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="captcha_records", indexes={@Index(name="idx_captcha_key", columnList="captcha_key"), @Index(name="idx_ip_address", columnList="ip_address"), @Index(name="idx_expires_at", columnList="expires_at"), @Index(name="idx_is_used", columnList="is_used"), @Index(name="idx_created_at", columnList="created_at")})
public class CaptchaRecord {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=64, name="captcha_key")
    private String captchaKey;
    @Column(nullable=false, length=64, name="captcha_code_hash")
    private String captchaCodeHash;
    @Column(nullable=false, length=50, name="ip_address")
    private String ipAddress;
    @Column(nullable=false, name="attempt_count")
    private Integer attemptCount = 0;
    @Column(nullable=false, name="is_used")
    private Boolean isUsed = false;
    @Column(nullable=false, name="expires_at")
    private LocalDateTime expiresAt;
    @Column(nullable=false, name="created_at", updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return this.id;
    }

    public String getCaptchaKey() {
        return this.captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptchaCodeHash() {
        return this.captchaCodeHash;
    }

    public void setCaptchaCodeHash(String captchaCodeHash) {
        this.captchaCodeHash = captchaCodeHash;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getAttemptCount() {
        return this.attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Boolean getIsUsed() {
        return this.isUsed;
    }

    public void setIsUsed(Boolean used) {
        this.isUsed = used;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CaptchaRecord)) {
            return false;
        }
        CaptchaRecord other = (CaptchaRecord)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$attemptCount = this.getAttemptCount();
        Integer other$attemptCount = other.getAttemptCount();
        if (this$attemptCount == null ? other$attemptCount != null : !((Object)this$attemptCount).equals(other$attemptCount)) {
            return false;
        }
        Boolean this$isUsed = this.getIsUsed();
        Boolean other$isUsed = other.getIsUsed();
        if (this$isUsed == null ? other$isUsed != null : !((Object)this$isUsed).equals(other$isUsed)) {
            return false;
        }
        String this$captchaKey = this.getCaptchaKey();
        String other$captchaKey = other.getCaptchaKey();
        if (this$captchaKey == null ? other$captchaKey != null : !this$captchaKey.equals(other$captchaKey)) {
            return false;
        }
        String this$captchaCodeHash = this.getCaptchaCodeHash();
        String other$captchaCodeHash = other.getCaptchaCodeHash();
        if (this$captchaCodeHash == null ? other$captchaCodeHash != null : !this$captchaCodeHash.equals(other$captchaCodeHash)) {
            return false;
        }
        String this$ipAddress = this.getIpAddress();
        String other$ipAddress = other.getIpAddress();
        if (this$ipAddress == null ? other$ipAddress != null : !this$ipAddress.equals(other$ipAddress)) {
            return false;
        }
        LocalDateTime this$expiresAt = this.getExpiresAt();
        LocalDateTime other$expiresAt = other.getExpiresAt();
        if (this$expiresAt == null ? other$expiresAt != null : !((Object)this$expiresAt).equals(other$expiresAt)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CaptchaRecord;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $attemptCount = this.getAttemptCount();
        result = result * 59 + ($attemptCount == null ? 43 : ((Object)$attemptCount).hashCode());
        Boolean $isUsed = this.getIsUsed();
        result = result * 59 + ($isUsed == null ? 43 : ((Object)$isUsed).hashCode());
        String $captchaKey = this.getCaptchaKey();
        result = result * 59 + ($captchaKey == null ? 43 : $captchaKey.hashCode());
        String $captchaCodeHash = this.getCaptchaCodeHash();
        result = result * 59 + ($captchaCodeHash == null ? 43 : $captchaCodeHash.hashCode());
        String $ipAddress = this.getIpAddress();
        result = result * 59 + ($ipAddress == null ? 43 : $ipAddress.hashCode());
        LocalDateTime $expiresAt = this.getExpiresAt();
        result = result * 59 + ($expiresAt == null ? 43 : ((Object)$expiresAt).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "CaptchaRecord(id=" + this.getId() + ", captchaKey=" + this.getCaptchaKey() + ", captchaCodeHash=" + this.getCaptchaCodeHash() + ", ipAddress=" + this.getIpAddress() + ", attemptCount=" + this.getAttemptCount() + ", isUsed=" + this.getIsUsed() + ", expiresAt=" + this.getExpiresAt() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

