package com.athlunakms.shopify.store.dto;

import com.athlunakms.shopify.store.dto.ShopInfoDto;
import com.athlunakms.shopify.store.dto.ShopifyVerifyResponse;

/*
 * Exception performing whole class analysis ignored.
 */
public class ShopifyVerifyResponse {
    private boolean success;
    private ShopInfoDto shopInfo;
    private String error;
    private String errorCode;

    public static ShopifyVerifyResponse success(ShopInfoDto shopInfo) {
        return ShopifyVerifyResponse.builder().success(true).shopInfo(shopInfo).build();
    }

    public static ShopifyVerifyResponse error(String message, String code) {
        return ShopifyVerifyResponse.builder().success(false).error(message).errorCode(code).build();
    }

    public static ShopifyVerifyResponseBuilder builder() {
        return new ShopifyVerifyResponseBuilder();
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ShopInfoDto getShopInfo() {
        return this.shopInfo;
    }

    public String getError() {
        return this.error;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setShopInfo(ShopInfoDto shopInfo) {
        this.shopInfo = shopInfo;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ShopifyVerifyResponse)) {
            return false;
        }
        ShopifyVerifyResponse other = (ShopifyVerifyResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        ShopInfoDto this$shopInfo = this.getShopInfo();
        ShopInfoDto other$shopInfo = other.getShopInfo();
        if (this$shopInfo == null ? other$shopInfo != null : !this$shopInfo.equals(other$shopInfo)) {
            return false;
        }
        String this$error = this.getError();
        String other$error = other.getError();
        if (this$error == null ? other$error != null : !this$error.equals(other$error)) {
            return false;
        }
        String this$errorCode = this.getErrorCode();
        String other$errorCode = other.getErrorCode();
        return !(this$errorCode == null ? other$errorCode != null : !this$errorCode.equals(other$errorCode));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ShopifyVerifyResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        ShopInfoDto $shopInfo = this.getShopInfo();
        result = result * 59 + ($shopInfo == null ? 43 : $shopInfo.hashCode());
        String $error = this.getError();
        result = result * 59 + ($error == null ? 43 : $error.hashCode());
        String $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        return result;
    }

    public String toString() {
        return "ShopifyVerifyResponse(success=" + this.isSuccess() + ", shopInfo=" + this.getShopInfo() + ", error=" + this.getError() + ", errorCode=" + this.getErrorCode() + ")";
    }

    public ShopifyVerifyResponse() {
    }

    public ShopifyVerifyResponse(boolean success, ShopInfoDto shopInfo, String error, String errorCode) {
        this.success = success;
        this.shopInfo = shopInfo;
        this.error = error;
        this.errorCode = errorCode;
    }

    public static class ShopifyVerifyResponseBuilder {
        private boolean success;
        private ShopInfoDto shopInfo;
        private String error;
        private String errorCode;

        ShopifyVerifyResponseBuilder() {
        }

        public ShopifyVerifyResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ShopifyVerifyResponseBuilder shopInfo(ShopInfoDto shopInfo) {
            this.shopInfo = shopInfo;
            return this;
        }

        public ShopifyVerifyResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ShopifyVerifyResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ShopifyVerifyResponse build() {
            return new ShopifyVerifyResponse(this.success, this.shopInfo, this.error, this.errorCode);
        }

        public String toString() {
            return "ShopifyVerifyResponse.ShopifyVerifyResponseBuilder(success=" + this.success + ", shopInfo=" + this.shopInfo + ", error=" + this.error + ", errorCode=" + this.errorCode + ")";
        }
    }
}

