package com.athlunakms.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordRequest {
    @NotBlank(message="\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(min=8, max=32, message="\u5bc6\u7801\u957f\u5ea6\u5fc5\u987b\u57288-32\u4e2a\u5b57\u7b26\u4e4b\u95f4")
    private @NotBlank(message="\u65b0\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") @Size(min=8, max=32, message="\u5bc6\u7801\u957f\u5ea6\u5fc5\u987b\u57288-32\u4e2a\u5b57\u7b26\u4e4b\u95f4") String newPassword;

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserPasswordRequest)) {
            return false;
        }
        UserPasswordRequest other = (UserPasswordRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$newPassword = this.getNewPassword();
        String other$newPassword = other.getNewPassword();
        return !(this$newPassword == null ? other$newPassword != null : !this$newPassword.equals(other$newPassword));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserPasswordRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $newPassword = this.getNewPassword();
        result = result * 59 + ($newPassword == null ? 43 : $newPassword.hashCode());
        return result;
    }

    public String toString() {
        return "UserPasswordRequest(newPassword=" + this.getNewPassword() + ")";
    }
}

