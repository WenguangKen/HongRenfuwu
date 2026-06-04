package com.athlunakms.eccang.store.dto;

import jakarta.validation.constraints.NotBlank;

public class EccangVerifyRequest {
    @NotBlank(message="\u5e97\u94faURL\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5e97\u94faURL\u4e0d\u80fd\u4e3a\u7a7a") String storeUrl;
    @NotBlank(message="Access Token\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="Access Token\u4e0d\u80fd\u4e3a\u7a7a") String accessToken;

    public String getStoreUrl() {
        return this.storeUrl;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EccangVerifyRequest)) {
            return false;
        }
        EccangVerifyRequest other = (EccangVerifyRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$storeUrl = this.getStoreUrl();
        String other$storeUrl = other.getStoreUrl();
        if (this$storeUrl == null ? other$storeUrl != null : !this$storeUrl.equals(other$storeUrl)) {
            return false;
        }
        String this$accessToken = this.getAccessToken();
        String other$accessToken = other.getAccessToken();
        return !(this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EccangVerifyRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $storeUrl = this.getStoreUrl();
        result = result * 59 + ($storeUrl == null ? 43 : $storeUrl.hashCode());
        String $accessToken = this.getAccessToken();
        result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
        return result;
    }

    public String toString() {
        return "EccangVerifyRequest(storeUrl=" + this.getStoreUrl() + ", accessToken=" + this.getAccessToken() + ")";
    }
}

