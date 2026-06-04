package com.athlunakms.eccang.store.dto;

import com.athlunakms.eccang.store.dto.ShopInfoDto;
import com.athlunakms.eccang.store.dto.EccangVerifyResponse;

/*
 * Exception performing whole class analysis ignored.
 */
public class EccangVerifyResponse {
    private boolean success;
    private ShopInfoDto shopInfo;
    private String error;
    private String errorCode;

    public static EccangVerifyResponse success(ShopInfoDto shopInfo) {
        return EccangVerifyResponse.builder().success(true).shopInfo(shopInfo).build();
    }

    public static EccangVerifyResponse error(String message, String code) {
        return EccangVerifyResponse.builder().success(false).error(message).errorCode(code).build();
    }

    public static EccangVerifyResponseBuilder builder() {
        return new EccangVerifyResponseBuilder();
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
        if (!(o instanceof EccangVerifyResponse)) {
            return false;
        }
        EccangVerifyResponse other = (EccangVerifyResponse)o;
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
        return other instanceof EccangVerifyResponse;
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
        return "EccangVerifyResponse(success=" + this.isSuccess() + ", shopInfo=" + this.getShopInfo() + ", error=" + this.getError() + ", errorCode=" + this.getErrorCode() + ")";
    }

    public EccangVerifyResponse() {
    }

    public EccangVerifyResponse(boolean success, ShopInfoDto shopInfo, String error, String errorCode) {
        this.success = success;
        this.shopInfo = shopInfo;
        this.error = error;
        this.errorCode = errorCode;
    }

    public static class EccangVerifyResponseBuilder {
        private boolean success;
        private ShopInfoDto shopInfo;
        private String error;
        private String errorCode;

        EccangVerifyResponseBuilder() {
        }

        public EccangVerifyResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public EccangVerifyResponseBuilder shopInfo(ShopInfoDto shopInfo) {
            this.shopInfo = shopInfo;
            return this;
        }

        public EccangVerifyResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public EccangVerifyResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public EccangVerifyResponse build() {
            return new EccangVerifyResponse(this.success, this.shopInfo, this.error, this.errorCode);
        }

        public String toString() {
            return "EccangVerifyResponse.EccangVerifyResponseBuilder(success=" + this.success + ", shopInfo=" + this.shopInfo + ", error=" + this.error + ", errorCode=" + this.errorCode + ")";
        }
    }
}

