package com.athlunakms.auth.dto;

public class CaptchaResponse {
    private String captchaKey;
    private String captchaImage;

    public CaptchaResponse(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptchaKey() {
        return this.captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptchaImage() {
        return this.captchaImage;
    }

    public void setCaptchaImage(String captchaImage) {
        this.captchaImage = captchaImage;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CaptchaResponse)) {
            return false;
        }
        CaptchaResponse other = (CaptchaResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$captchaKey = this.getCaptchaKey();
        String other$captchaKey = other.getCaptchaKey();
        if (this$captchaKey == null ? other$captchaKey != null : !this$captchaKey.equals(other$captchaKey)) {
            return false;
        }
        String this$captchaImage = this.getCaptchaImage();
        String other$captchaImage = other.getCaptchaImage();
        return !(this$captchaImage == null ? other$captchaImage != null : !this$captchaImage.equals(other$captchaImage));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CaptchaResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $captchaKey = this.getCaptchaKey();
        result = result * 59 + ($captchaKey == null ? 43 : $captchaKey.hashCode());
        String $captchaImage = this.getCaptchaImage();
        result = result * 59 + ($captchaImage == null ? 43 : $captchaImage.hashCode());
        return result;
    }

    public String toString() {
        return "CaptchaResponse(captchaKey=" + this.getCaptchaKey() + ", captchaImage=" + this.getCaptchaImage() + ")";
    }
}

