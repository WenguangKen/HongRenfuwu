package com.athlunakms.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name="user_sessions", indexes={@Index(name="idx_user_id", columnList="user_id"), @Index(name="idx_token_hash", columnList="token_hash"), @Index(name="idx_expires_at", columnList="expires_at"), @Index(name="idx_is_active", columnList="is_active"), @Index(name="idx_last_activity_time", columnList="last_activity_time")}, uniqueConstraints={@UniqueConstraint(name="uk_active_user", columnNames={"active_user_id"})})
public class UserSession {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, name="user_id")
    private Long userId;
    @Column(nullable=false, unique=true, length=64, name="token_hash")
    private String tokenHash;
    @Column(length=500, name="device_info")
    private String deviceInfo;
    @Column(nullable=false, length=50, name="ip_address")
    private String ipAddress;
    @Column(length=200)
    private String location;
    @Column(nullable=false, name="login_time")
    private LocalDateTime loginTime = LocalDateTime.now();
    @Column(nullable=false, name="last_activity_time")
    private LocalDateTime lastActivityTime = LocalDateTime.now();
    @Column(nullable=false, name="expires_at")
    private LocalDateTime expiresAt;
    @Column(nullable=false, name="is_active")
    private Boolean isActive = true;
    @Column(name="active_user_id", insertable=false, updatable=false)
    private Long activeUserId;

    @PreUpdate
    public void updateActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getTokenHash() {
        return this.tokenHash;
    }

    public String getDeviceInfo() {
        return this.deviceInfo;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDateTime getLastActivityTime() {
        return this.lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getLoginTime() {
        return this.loginTime;
    }

    public Long getActiveUserId() {
        return this.activeUserId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActiveUserId(Long activeUserId) {
        this.activeUserId = activeUserId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$userId = this.getUserId();
        Long other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !((Object)this$userId).equals(other$userId)) {
            return false;
        }
        Boolean this$isActive = this.getIsActive();
        Boolean other$isActive = other.getIsActive();
        if (this$isActive == null ? other$isActive != null : !((Object)this$isActive).equals(other$isActive)) {
            return false;
        }
        Long this$activeUserId = this.getActiveUserId();
        Long other$activeUserId = other.getActiveUserId();
        if (this$activeUserId == null ? other$activeUserId != null : !((Object)this$activeUserId).equals(other$activeUserId)) {
            return false;
        }
        String this$tokenHash = this.getTokenHash();
        String other$tokenHash = other.getTokenHash();
        if (this$tokenHash == null ? other$tokenHash != null : !this$tokenHash.equals(other$tokenHash)) {
            return false;
        }
        String this$deviceInfo = this.getDeviceInfo();
        String other$deviceInfo = other.getDeviceInfo();
        if (this$deviceInfo == null ? other$deviceInfo != null : !this$deviceInfo.equals(other$deviceInfo)) {
            return false;
        }
        String this$ipAddress = this.getIpAddress();
        String other$ipAddress = other.getIpAddress();
        if (this$ipAddress == null ? other$ipAddress != null : !this$ipAddress.equals(other$ipAddress)) {
            return false;
        }
        String this$location = this.getLocation();
        String other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) {
            return false;
        }
        LocalDateTime this$loginTime = this.getLoginTime();
        LocalDateTime other$loginTime = other.getLoginTime();
        if (this$loginTime == null ? other$loginTime != null : !((Object)this$loginTime).equals(other$loginTime)) {
            return false;
        }
        LocalDateTime this$lastActivityTime = this.getLastActivityTime();
        LocalDateTime other$lastActivityTime = other.getLastActivityTime();
        if (this$lastActivityTime == null ? other$lastActivityTime != null : !((Object)this$lastActivityTime).equals(other$lastActivityTime)) {
            return false;
        }
        LocalDateTime this$expiresAt = this.getExpiresAt();
        LocalDateTime other$expiresAt = other.getExpiresAt();
        return !(this$expiresAt == null ? other$expiresAt != null : !((Object)this$expiresAt).equals(other$expiresAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserSession;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        Boolean $isActive = this.getIsActive();
        result = result * 59 + ($isActive == null ? 43 : ((Object)$isActive).hashCode());
        Long $activeUserId = this.getActiveUserId();
        result = result * 59 + ($activeUserId == null ? 43 : ((Object)$activeUserId).hashCode());
        String $tokenHash = this.getTokenHash();
        result = result * 59 + ($tokenHash == null ? 43 : $tokenHash.hashCode());
        String $deviceInfo = this.getDeviceInfo();
        result = result * 59 + ($deviceInfo == null ? 43 : $deviceInfo.hashCode());
        String $ipAddress = this.getIpAddress();
        result = result * 59 + ($ipAddress == null ? 43 : $ipAddress.hashCode());
        String $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : $location.hashCode());
        LocalDateTime $loginTime = this.getLoginTime();
        result = result * 59 + ($loginTime == null ? 43 : ((Object)$loginTime).hashCode());
        LocalDateTime $lastActivityTime = this.getLastActivityTime();
        result = result * 59 + ($lastActivityTime == null ? 43 : ((Object)$lastActivityTime).hashCode());
        LocalDateTime $expiresAt = this.getExpiresAt();
        result = result * 59 + ($expiresAt == null ? 43 : ((Object)$expiresAt).hashCode());
        return result;
    }

    public String toString() {
        return "UserSession(id=" + this.getId() + ", userId=" + this.getUserId() + ", tokenHash=" + this.getTokenHash() + ", deviceInfo=" + this.getDeviceInfo() + ", ipAddress=" + this.getIpAddress() + ", location=" + this.getLocation() + ", loginTime=" + this.getLoginTime() + ", lastActivityTime=" + this.getLastActivityTime() + ", expiresAt=" + this.getExpiresAt() + ", isActive=" + this.getIsActive() + ", activeUserId=" + this.getActiveUserId() + ")";
    }
}

