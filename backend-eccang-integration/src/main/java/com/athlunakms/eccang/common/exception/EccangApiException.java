package com.athlunakms.eccang.common.exception;

public class EccangApiException
extends RuntimeException {
    private final String errorCode;
    private final int httpStatus;

    public EccangApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 400;
    }

    public EccangApiException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public EccangApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ECCANG_ERROR";
        this.httpStatus = 500;
    }

    public EccangApiException(String message, String errorCode, Throwable cause) {
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

