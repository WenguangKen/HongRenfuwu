package com.athlunakms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UserCreateRequest {
    @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max=50, message="\u7528\u6237\u540d\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26")
    private @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a") @Size(max=50, message="\u7528\u6237\u540d\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26") String username;
    @NotBlank(message="\u90ae\u7bb1\u4e0d\u80fd\u4e3a\u7a7a")
    @Email(message="\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e")
    private @NotBlank(message="\u90ae\u7bb1\u4e0d\u80fd\u4e3a\u7a7a") @Email(message="\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e") String email;
    @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(min=8, max=32, message="\u5bc6\u7801\u957f\u5ea6\u5fc5\u987b\u57288-32\u4e2a\u5b57\u7b26\u4e4b\u95f4")
    private @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") @Size(min=8, max=32, message="\u5bc6\u7801\u957f\u5ea6\u5fc5\u987b\u57288-32\u4e2a\u5b57\u7b26\u4e4b\u95f4") String password;
    @Size(max=20, message="\u7535\u8bdd\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc720\u4e2a\u5b57\u7b26")
    private @Size(max=20, message="\u7535\u8bdd\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc720\u4e2a\u5b57\u7b26") String phone;
    @NotNull(message="\u89d2\u8272ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u89d2\u8272ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a") List<Long> roleIds;
    private String avatarUrl;

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
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

    public void setPassword(String password) {
        this.password = password;
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
        if (!(o instanceof UserCreateRequest)) {
            return false;
        }
        UserCreateRequest other = (UserCreateRequest)o;
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
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
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
        return other instanceof UserCreateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        List $roleIds = this.getRoleIds();
        result = result * 59 + ($roleIds == null ? 43 : ((Object)$roleIds).hashCode());
        String $avatarUrl = this.getAvatarUrl();
        result = result * 59 + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
        return result;
    }

    public String toString() {
        return "UserCreateRequest(username=" + this.getUsername() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", phone=" + this.getPhone() + ", roleIds=" + this.getRoleIds() + ", avatarUrl=" + this.getAvatarUrl() + ")";
    }
}

