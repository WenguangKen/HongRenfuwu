package com.athlunakms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UserUpdateRequest {
    @Size(max=50, message="\u7528\u6237\u540d\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26")
    private @Size(max=50, message="\u7528\u6237\u540d\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26") String username;
    @Email(message="\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e")
    private @Email(message="\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e") String email;
    @Size(max=20, message="\u7535\u8bdd\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc720\u4e2a\u5b57\u7b26")
    private @Size(max=20, message="\u7535\u8bdd\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc720\u4e2a\u5b57\u7b26") String phone;
    private List<Long> roleIds;
    private String avatarUrl;
    private List<String> allocatedStores;

    public List<String> getAllocatedStores() {
        return this.allocatedStores;
    }

    public void setAllocatedStores(List<String> allocatedStores) {
        this.allocatedStores = allocatedStores;
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

    public List<Long> getRoleIds() {
        return this.roleIds;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
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

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserUpdateRequest)) {
            return false;
        }
        UserUpdateRequest other = (UserUpdateRequest)o;
        if (!other.canEqual((Object)this)) {
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
        List this$roleIds = this.getRoleIds();
        List other$roleIds = other.getRoleIds();
        if (this$roleIds == null ? other$roleIds != null : !((Object)this$roleIds).equals(other$roleIds)) {
            return false;
        }
        String this$avatarUrl = this.getAvatarUrl();
        String other$avatarUrl = other.getAvatarUrl();
        return !(this$avatarUrl == null ? other$avatarUrl != null : !this$avatarUrl.equals(other$avatarUrl));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        List $roleIds = this.getRoleIds();
        result = result * 59 + ($roleIds == null ? 43 : ((Object)$roleIds).hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
        return result;
    }

    public String toString() {
        return "UserUpdateRequest(username=" + this.getUsername() + ", email=" + this.getEmail() + ", phone=" + this.getPhone() + ", roleIds=" + this.getRoleIds() + ", avatarUrl=" + this.getAvatarUrl() + ")";
    }
}

