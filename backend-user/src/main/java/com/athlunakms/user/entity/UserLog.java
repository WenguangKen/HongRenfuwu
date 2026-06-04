package com.athlunakms.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="user_logs", indexes={@Index(name="idx_user_id", columnList="user_id"), @Index(name="idx_action_type", columnList="action_type"), @Index(name="idx_created_at", columnList="created_at")})
public class UserLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, name="user_id")
    private Long userId;
    @Column(nullable=false, length=50, name="action_type")
    private String actionType;
    @Column(length=500, name="action_desc")
    private String actionDesc;
    @Column(length=50, name="ip_address")
    private String ipAddress;
    @Column(length=500, name="user_agent")
    private String userAgent;
    @Column(columnDefinition="JSON", name="details")
    private String details;
    @Column(length=100, name="location")
    private String location;
    @Column(nullable=false, name="created_at", updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return this.id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return this.actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDesc() {
        return this.actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserLog)) {
            return false;
        }
        UserLog other = (UserLog)o;
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
        String this$actionType = this.getActionType();
        String other$actionType = other.getActionType();
        if (this$actionType == null ? other$actionType != null : !this$actionType.equals(other$actionType)) {
            return false;
        }
        String this$actionDesc = this.getActionDesc();
        String other$actionDesc = other.getActionDesc();
        if (this$actionDesc == null ? other$actionDesc != null : !this$actionDesc.equals(other$actionDesc)) {
            return false;
        }
        String this$ipAddress = this.getIpAddress();
        String other$ipAddress = other.getIpAddress();
        if (this$ipAddress == null ? other$ipAddress != null : !this$ipAddress.equals(other$ipAddress)) {
            return false;
        }
        String this$userAgent = this.getUserAgent();
        String other$userAgent = other.getUserAgent();
        if (this$userAgent == null ? other$userAgent != null : !this$userAgent.equals(other$userAgent)) {
            return false;
        }
        String this$details = this.getDetails();
        String other$details = other.getDetails();
        if (this$details == null ? other$details != null : !this$details.equals(other$details)) {
            return false;
        }
        String this$location = this.getLocation();
        String other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserLog;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        String $actionType = this.getActionType();
        result = result * 59 + ($actionType == null ? 43 : $actionType.hashCode());
        String $actionDesc = this.getActionDesc();
        result = result * 59 + ($actionDesc == null ? 43 : $actionDesc.hashCode());
        String $ipAddress = this.getIpAddress();
        result = result * 59 + ($ipAddress == null ? 43 : $ipAddress.hashCode());
        String $userAgent = this.getUserAgent();
        result = result * 59 + ($userAgent == null ? 43 : $userAgent.hashCode());
        String $details = this.getDetails();
        result = result * 59 + ($details == null ? 43 : $details.hashCode());
        String $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : $location.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "UserLog(id=" + this.getId() + ", userId=" + this.getUserId() + ", actionType=" + this.getActionType() + ", actionDesc=" + this.getActionDesc() + ", ipAddress=" + this.getIpAddress() + ", userAgent=" + this.getUserAgent() + ", details=" + this.getDetails() + ", location=" + this.getLocation() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

