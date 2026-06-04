package com.athlunakms.shopify.common.exception;

public class ShopifyApiException
extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;

    public ShopifyApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }

    public ShopifyApiException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ShopifyApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SHOPIFY_ERROR";
        this.httpStatus = 500;
    }

    public ShopifyApiException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = 500;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }
}

