package com.athlunakms.common.exception;

public enum ErrorCode {
    SUCCESS(200, "\u6210\u529f"),
    BAD_REQUEST(400, "\u8bf7\u6c42\u53c2\u6570\u9519\u8bef"),
    UNAUTHORIZED(401, "\u672a\u6388\u6743"),
    FORBIDDEN(403, "\u65e0\u6743\u9650"),
    NOT_FOUND(404, "\u8d44\u6e90\u4e0d\u5b58\u5728"),
    INTERNAL_ERROR(500, "\u670d\u52a1\u5668\u5185\u90e8\u9519\u8bef"),
    LOGIN_FAILED(2001, "\u767b\u5f55\u5931\u8d25"),
    INVALID_CREDENTIALS(2002, "\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef"),
    CAPTCHA_ERROR(2003, "\u9a8c\u8bc1\u7801\u9519\u8bef"),
    CAPTCHA_EXPIRED(2004, "\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f"),
    CAPTCHA_USED(2005, "\u9a8c\u8bc1\u7801\u5df2\u4f7f\u7528"),
    TOKEN_EXPIRED(2006, "Token\u5df2\u8fc7\u671f"),
    TOKEN_INVALID(2007, "Token\u65e0\u6548"),
    ACCOUNT_LOCKED(2008, "\u8d26\u6237\u5df2\u88ab\u9501\u5b9a"),
    ACCOUNT_INACTIVE(2009, "\u8d26\u6237\u672a\u6fc0\u6d3b"),
    SESSION_EXPIRED(2010, "\u4f1a\u8bdd\u5df2\u8fc7\u671f"),
    SSO_CONFLICT(2011, "\u5355\u70b9\u767b\u5f55\u51b2\u7a81"),
    RATE_LIMIT_EXCEEDED(2012, "\u8bf7\u6c42\u8fc7\u4e8e\u9891\u7e41\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5"),
    USER_NOT_FOUND(3001, "\u7528\u6237\u4e0d\u5b58\u5728"),
    USER_ALREADY_EXISTS(3002, "\u7528\u6237\u5df2\u5b58\u5728"),
    EMAIL_ALREADY_EXISTS(3003, "\u90ae\u7bb1\u5df2\u88ab\u4f7f\u7528"),
    PASSWORD_WEAK(3004, "\u5bc6\u7801\u5f3a\u5ea6\u4e0d\u8db3"),
    PASSWORD_MISMATCH(3005, "\u5bc6\u7801\u4e0d\u5339\u914d"),
    ROLE_NOT_FOUND(4001, "\u89d2\u8272\u4e0d\u5b58\u5728"),
    ROLE_ALREADY_EXISTS(4002, "\u89d2\u8272\u5df2\u5b58\u5728"),
    ROLE_IN_USE(4003, "\u89d2\u8272\u6b63\u5728\u4f7f\u7528\u4e2d\uff0c\u65e0\u6cd5\u5220\u9664"),
    PERMISSION_NOT_FOUND(5001, "\u6743\u9650\u4e0d\u5b58\u5728"),
    PERMISSION_DENIED(5002, "\u6743\u9650\u4e0d\u8db3"),
    DATA_NOT_FOUND(6001, "\u6570\u636e\u4e0d\u5b58\u5728"),
    DATA_CONFLICT(6002, "\u6570\u636e\u51b2\u7a81"),
    OPTIMISTIC_LOCK_FAILED(6003, "\u6570\u636e\u5df2\u88ab\u4fee\u6539\uff0c\u8bf7\u5237\u65b0\u540e\u91cd\u8bd5"),
    ENCRYPTION_ERROR(7001, "\u52a0\u5bc6\u5931\u8d25"),
    DECRYPTION_ERROR(7002, "\u89e3\u5bc6\u5931\u8d25"),
    KEY_ROTATION_ERROR(7003, "\u5bc6\u94a5\u8f6e\u6362\u5931\u8d25");

    private final int code;
    private final String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

