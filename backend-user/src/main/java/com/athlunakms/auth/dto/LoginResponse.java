package com.athlunakms.auth.dto;

import com.athlunakms.auth.dto.LoginResponse;
import com.athlunakms.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private UserInfo user;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return this.expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUser() {
        return this.user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginResponse)) {
            return false;
        }
        LoginResponse other = (LoginResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$expiresIn = this.getExpiresIn();
        Long other$expiresIn = other.getExpiresIn();
        if (this$expiresIn == null ? other$expiresIn != null : !((Object)this$expiresIn).equals(other$expiresIn)) {
            return false;
        }
        String this$token = this.getToken();
        String other$token = other.getToken();
        if (this$token == null ? other$token != null : !this$token.equals(other$token)) {
            return false;
        }
        String this$refreshToken = this.getRefreshToken();
        String other$refreshToken = other.getRefreshToken();
        if (this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken)) {
            return false;
        }
        UserInfo this$user = this.getUser();
        UserInfo other$user = other.getUser();
        return !(this$user == null ? other$user != null : !this$user.equals(other$user));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $expiresIn = this.getExpiresIn();
        result = result * 59 + ($expiresIn == null ? 43 : ((Object)$expiresIn).hashCode());
        String $token = this.getToken();
        result = result * 59 + ($token == null ? 43 : $token.hashCode());
        String $refreshToken = this.getRefreshToken();
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        UserInfo $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public String toString() {
        return "LoginResponse(token=" + this.getToken() + ", refreshToken=" + this.getRefreshToken() + ", expiresIn=" + this.getExpiresIn() + ", user=" + this.getUser() + ")";
    }

    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String avatarUrl;
        private List<RoleInfo> roles;
        private String lastLoginIp;
        private String lastLoginLocation;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createdAt;
        private User.UserStatus status;

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

        public String getAvatarUrl() {
            return this.avatarUrl;
        }

        public List<RoleInfo> getRoles() {
            return this.roles;
        }

        public String getLastLoginIp() {
            return this.lastLoginIp;
        }

        public String getLastLoginLocation() {
            return this.lastLoginLocation;
        }

        public LocalDateTime getLastLoginTime() {
            return this.lastLoginTime;
        }

        public LocalDateTime getCreatedAt() {
            return this.createdAt;
        }

        public User.UserStatus getStatus() {
            return this.status;
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

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setRoles(List<RoleInfo> roles) {
            this.roles = roles;
        }

        public void setLastLoginIp(String lastLoginIp) {
            this.lastLoginIp = lastLoginIp;
        }

        public void setLastLoginLocation(String lastLoginLocation) {
            this.lastLoginLocation = lastLoginLocation;
        }

        public void setLastLoginTime(LocalDateTime lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public void setStatus(User.UserStatus status) {
            this.status = status;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UserInfo)) {
                return false;
            }
            UserInfo other = (UserInfo)o;
            if (!other.canEqual(this)) {
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
            String this$avatarUrl = this.getAvatarUrl();
            String other$avatarUrl = other.getAvatarUrl();
            if (this$avatarUrl == null ? other$avatarUrl != null : !this$avatarUrl.equals(other$avatarUrl)) {
                return false;
            }
            List<RoleInfo> this$roles = this.getRoles();
            List<RoleInfo> other$roles = other.getRoles();
            if (this$roles == null ? other$roles != null : !((Object)this$roles).equals(other$roles)) {
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
            LocalDateTime this$lastLoginTime = this.getLastLoginTime();
            LocalDateTime other$lastLoginTime = other.getLastLoginTime();
            if (this$lastLoginTime == null ? other$lastLoginTime != null : !((Object)this$lastLoginTime).equals(other$lastLoginTime)) {
                return false;
            }
            LocalDateTime this$createdAt = this.getCreatedAt();
            LocalDateTime other$createdAt = other.getCreatedAt();
            if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
                return false;
            }
            User.UserStatus this$status = this.getStatus();
            User.UserStatus other$status = other.getStatus();
            return !(this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status));
        }

        protected boolean canEqual(Object other) {
            return other instanceof UserInfo;
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
            String $avatarUrl = this.getAvatarUrl();
            result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
            List<RoleInfo> $roles = this.getRoles();
            result = result * 59 + ($roles == null ? 43 : ((Object)$roles).hashCode());
            String $lastLoginIp = this.getLastLoginIp();
            result = result * 59 + ($lastLoginIp == null ? 43 : $lastLoginIp.hashCode());
            String $lastLoginLocation = this.getLastLoginLocation();
            result = result * 59 + ($lastLoginLocation == null ? 43 : $lastLoginLocation.hashCode());
            LocalDateTime $lastLoginTime = this.getLastLoginTime();
            result = result * 59 + ($lastLoginTime == null ? 43 : ((Object)$lastLoginTime).hashCode());
            LocalDateTime $createdAt = this.getCreatedAt();
            result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
            User.UserStatus $status = this.getStatus();
            result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
            return result;
        }

        public String toString() {
            return "LoginResponse.UserInfo(id=" + this.getId() + ", username=" + this.getUsername() + ", email=" + this.getEmail() + ", phone=" + this.getPhone() + ", avatarUrl=" + this.getAvatarUrl() + ", roles=" + this.getRoles() + ", lastLoginIp=" + this.getLastLoginIp() + ", lastLoginLocation=" + this.getLastLoginLocation() + ", lastLoginTime=" + this.getLastLoginTime() + ", createdAt=" + this.getCreatedAt() + ", status=" + this.getStatus() + ")";
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
                return "LoginResponse.UserInfo.RoleInfo(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ")";
            }
        }
    }
}

