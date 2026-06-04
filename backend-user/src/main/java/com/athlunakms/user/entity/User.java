package com.athlunakms.user.entity;

import com.athlunakms.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name="users", indexes={@Index(name="idx_email_hash", columnList="email_hash"), @Index(name="idx_status", columnList="status"), @Index(name="idx_last_activity_time", columnList="last_activity_time")})
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=50)
    private String username;
    @Column(nullable=false, unique=true, length=64, name="email_hash")
    private String emailHash;
    @Column(nullable=false, length=500, name="email_encrypted")
    private String emailEncrypted;
    @Column(length=64, name="phone_hash")
    private String phoneHash;
    @Column(length=500, name="phone_encrypted")
    private String phoneEncrypted;
    @Column(nullable=false, length=255, name="password_hash")
    private String passwordHash;
    @Enumerated(value=EnumType.ORDINAL)
    @Column(nullable=false)
    private UserStatus status = UserStatus.active;
    @Column(length=500, name="avatar_url")
    private String avatarUrl;
    @Column(name="last_login_time")
    private LocalDateTime lastLoginTime;
    @Column(length=50, name="last_login_ip")
    private String lastLoginIp;
    @Column(length=100, name="last_login_location")
    private String lastLoginLocation;
    @Column(name="last_activity_time")
    private LocalDateTime lastActivityTime;
    @Version
    @Column(nullable=false)
    private Integer version = 0;
    @Column(nullable=false, name="created_at", updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable=false, name="updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmailHash() {
        return this.emailHash;
    }

    public String getEmailEncrypted() {
        return this.emailEncrypted;
    }

    public String getPhoneHash() {
        return this.phoneHash;
    }

    public String getPhoneEncrypted() {
        return this.phoneEncrypted;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public LocalDateTime getLastLoginTime() {
        return this.lastLoginTime;
    }

    public String getLastLoginIp() {
        return this.lastLoginIp;
    }

    public LocalDateTime getLastActivityTime() {
        return this.lastActivityTime;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public void setEmailEncrypted(String emailEncrypted) {
        this.emailEncrypted = emailEncrypted;
    }

    public void setPhoneHash(String phoneHash) {
        this.phoneHash = phoneHash;
    }

    public void setPhoneEncrypted(String phoneEncrypted) {
        this.phoneEncrypted = phoneEncrypted;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastLoginLocation() {
        return this.lastLoginLocation;
    }

    public void setLastLoginLocation(String lastLoginLocation) {
        this.lastLoginLocation = lastLoginLocation;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String toString() {
        return "User(id=" + this.getId() + ", username=" + this.getUsername() + ", emailHash=" + this.getEmailHash() + ", emailEncrypted=" + this.getEmailEncrypted() + ", phoneHash=" + this.getPhoneHash() + ", phoneEncrypted=" + this.getPhoneEncrypted() + ", passwordHash=" + this.getPasswordHash() + ", status=" + this.getStatus() + ", avatarUrl=" + this.getAvatarUrl() + ", lastLoginTime=" + this.getLastLoginTime() + ", lastLoginIp=" + this.getLastLoginIp() + ", lastLoginLocation=" + this.getLastLoginLocation() + ", lastActivityTime=" + this.getLastActivityTime() + ", version=" + this.getVersion() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User other = (User)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$version = this.getVersion();
        Integer other$version = other.getVersion();
        if (this$version == null ? other$version != null : !((Object)this$version).equals(other$version)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$emailHash = this.getEmailHash();
        String other$emailHash = other.getEmailHash();
        if (this$emailHash == null ? other$emailHash != null : !this$emailHash.equals(other$emailHash)) {
            return false;
        }
        String this$emailEncrypted = this.getEmailEncrypted();
        String other$emailEncrypted = other.getEmailEncrypted();
        if (this$emailEncrypted == null ? other$emailEncrypted != null : !this$emailEncrypted.equals(other$emailEncrypted)) {
            return false;
        }
        String this$phoneHash = this.getPhoneHash();
        String other$phoneHash = other.getPhoneHash();
        if (this$phoneHash == null ? other$phoneHash != null : !this$phoneHash.equals(other$phoneHash)) {
            return false;
        }
        String this$phoneEncrypted = this.getPhoneEncrypted();
        String other$phoneEncrypted = other.getPhoneEncrypted();
        if (this$phoneEncrypted == null ? other$phoneEncrypted != null : !this$phoneEncrypted.equals(other$phoneEncrypted)) {
            return false;
        }
        String this$passwordHash = this.getPasswordHash();
        String other$passwordHash = other.getPasswordHash();
        if (this$passwordHash == null ? other$passwordHash != null : !this$passwordHash.equals(other$passwordHash)) {
            return false;
        }
        UserStatus this$status = this.getStatus();
        UserStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$avatarUrl = this.getAvatarUrl();
        String other$avatarUrl = other.getAvatarUrl();
        if (this$avatarUrl == null ? other$avatarUrl != null : !this$avatarUrl.equals(other$avatarUrl)) {
            return false;
        }
        LocalDateTime this$lastLoginTime = this.getLastLoginTime();
        LocalDateTime other$lastLoginTime = other.getLastLoginTime();
        if (this$lastLoginTime == null ? other$lastLoginTime != null : !((Object)this$lastLoginTime).equals(other$lastLoginTime)) {
            return false;
        }
        String this$lastLoginIp = this.getLastLoginIp();
        String other$lastLoginIp = other.getLastLoginIp();
        if (this$lastLoginIp == null ? other$lastLoginIp != null : !this$lastLoginIp.equals(other$lastLoginIp)) {
            return false;
        }
        String this$lastLoginLocation = this.getLastLoginLocation();
        String other$lastLoginLocation = other.getLastLoginLocation();
        if (this$lastLoginLocation == null ? other$lastLoginLocation != null : !this$lastLoginLocation.equals(other$lastLoginLocation)) {
            return false;
        }
        LocalDateTime this$lastActivityTime = this.getLastActivityTime();
        LocalDateTime other$lastActivityTime = other.getLastActivityTime();
        if (this$lastActivityTime == null ? other$lastActivityTime != null : !((Object)this$lastActivityTime).equals(other$lastActivityTime)) {
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
        return other instanceof User;
    }

    public int hashCode() {
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : ((Object)$version).hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $emailHash = this.getEmailHash();
        result = result * 59 + ($emailHash == null ? 43 : $emailHash.hashCode());
        String $emailEncrypted = this.getEmailEncrypted();
        result = result * 59 + ($emailEncrypted == null ? 43 : $emailEncrypted.hashCode());
        String $phoneHash = this.getPhoneHash();
        result = result * 59 + ($phoneHash == null ? 43 : $phoneHash.hashCode());
        String $phoneEncrypted = this.getPhoneEncrypted();
        result = result * 59 + ($phoneEncrypted == null ? 43 : $phoneEncrypted.hashCode());
        String $passwordHash = this.getPasswordHash();
        result = result * 59 + ($passwordHash == null ? 43 : $passwordHash.hashCode());
        UserStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
        LocalDateTime $lastLoginTime = this.getLastLoginTime();
        result = result * 59 + ($lastLoginTime == null ? 43 : ((Object)$lastLoginTime).hashCode());
        String $lastLoginIp = this.getLastLoginIp();
        result = result * 59 + ($lastLoginIp == null ? 43 : $lastLoginIp.hashCode());
        String $lastLoginLocation = this.getLastLoginLocation();
        result = result * 59 + ($lastLoginLocation == null ? 43 : $lastLoginLocation.hashCode());
        LocalDateTime $lastActivityTime = this.getLastActivityTime();
        result = result * 59 + ($lastActivityTime == null ? 43 : ((Object)$lastActivityTime).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public static enum UserStatus {
        inactive,
        active,
        locked;
    }
}

