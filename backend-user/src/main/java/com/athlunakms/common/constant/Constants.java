package com.athlunakms.common.constant;

public class Constants {
    public static final String API_VERSION = "/api/v1.0";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String CAPTCHA_KEY_PREFIX = "captcha:";
    public static final String USER_PERMISSIONS_CACHE_PREFIX = "user_permissions:";
    public static final String USER_SESSION_CACHE_PREFIX = "user_session:";
    public static final String RATE_LIMIT_PREFIX = "rate_limit:";
    public static final String LOCK_PREFIX = "lock:";
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int SESSION_TIMEOUT_SECONDS = 14400;
    public static final int CAPTCHA_EXPIRE_MINUTES = 5;
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_DURATION = 1800;
}

