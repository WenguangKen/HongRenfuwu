package com.athlunakms.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message="\u8d26\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8d26\u53f7\u4e0d\u80fd\u4e3a\u7a7a") String email;
    @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") String password;
    private String captcha;
    private String captchaKey;

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getCaptcha() {
        return this.captcha;
    }

    public String getCaptchaKey() {
        return this.captchaKey;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginRequest)) {
            return false;
        }
        LoginRequest other = (LoginRequest)o;
        if (!other.canEqual((Object)this)) {
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
        String this$captcha = this.getCaptcha();
        String other$captcha = other.getCaptcha();
        if (this$captcha == null ? other$captcha != null : !this$captcha.equals(other$captcha)) {
            return false;
        }
        String this$captchaKey = this.getCaptchaKey();
        String other$captchaKey = other.getCaptchaKey();
        return !(this$captchaKey == null ? other$captchaKey != null : !this$captchaKey.equals(other$captchaKey));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $captcha = this.getCaptcha();
        result = result * 59 + ($captcha == null ? 43 : $captcha.hashCode());
        String $captchaKey = this.getCaptchaKey();
        result = result * 59 + ($captchaKey == null ? 43 : $captchaKey.hashCode());
        return result;
    }

    public String toString() {
        return "LoginRequest(email=" + this.getEmail() + ", password=" + this.getPassword() + ", captcha=" + this.getCaptcha() + ", captchaKey=" + this.getCaptchaKey() + ")";
    }
}

