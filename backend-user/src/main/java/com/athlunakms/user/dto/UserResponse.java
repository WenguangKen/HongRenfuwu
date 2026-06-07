package com.athlunakms.user.dto;

import com.athlunakms.user.dto.UserResponse;
import com.athlunakms.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private User.UserStatus status;
    private String avatarUrl;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private String lastLoginLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoleInfo> roles;
    private List<String> allocatedStores;

    public List<String> getAllocatedStores() {
        return this.allocatedStores;
    }

    public void setAllocatedStores(List<String> allocatedStores) {
        this.allocatedStores = allocatedStores;
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public User.UserStatus getStatus() {
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

    public String getLastLoginLocation() {
        return this.lastLoginLocation;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public List<RoleInfo> getRoles() {
        return this.roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(User.UserStatus status) {
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

    public void setLastLoginLocation(String lastLoginLocation) {
        this.lastLoginLocation = lastLoginLocation;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserResponse)) {
            return false;
        }
        UserResponse other = (UserResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        User.UserStatus this$status = this.getStatus();
        User.UserStatus other$status = other.getStatus();
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
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
            return false;
        }
        List this$roles = this.getRoles();
        List other$roles = other.getRoles();
        return !(this$roles == null ? other$roles != null : !((Object)this$roles).equals(other$roles));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        User.UserStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
        LocalDateTime $lastLoginTime = this.getLastLoginTime();
        result = result * 59 + ($lastLoginTime == null ? 43 : ((Object)$lastLoginTime).hashCode());
        String $lastLoginIp = this.getLastLoginIp();
        result = result * 59 + ($lastLoginIp == null ? 43 : $lastLoginIp.hashCode());
        String $lastLoginLocation = this.getLastLoginLocation();
        result = result * 59 + ($lastLoginLocation == null ? 43 : $lastLoginLocation.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        List $roles = this.getRoles();
        result = result * 59 + ($roles == null ? 43 : ((Object)$roles).hashCode());
        return result;
    }

    public String toString() {
        return "UserResponse(id=" + this.getId() + ", username=" + this.getUsername() + ", email=" + this.getEmail() + ", phone=" + this.getPhone() + ", status=" + this.getStatus() + ", avatarUrl=" + this.getAvatarUrl() + ", lastLoginTime=" + this.getLastLoginTime() + ", lastLoginIp=" + this.getLastLoginIp() + ", lastLoginLocation=" + this.getLastLoginLocation() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", roles=" + this.getRoles() + ")";
    }

    public static class RoleInfo {
        private Long id;
        private String name;
        private String description;

        public Long getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RoleInfo)) {
                return false;
            }
            RoleInfo other = (RoleInfo)o;
            if (!other.canEqual(this)) {
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
            String this$description = this.getDescription();
            String other$description = other.getDescription();
            return !(this$description == null ? other$description != null : !this$description.equals(other$description));
        }

        protected boolean canEqual(Object other) {
            return other instanceof RoleInfo;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Long $id = this.getId();
            result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
            String $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            String $description = this.getDescription();
            result = result * 59 + ($description == null ? 43 : $description.hashCode());
            return result;
        }

        public String toString() {
            return "UserResponse.RoleInfo(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ")";
        }
    }
}

