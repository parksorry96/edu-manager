package com.edumanager.common;

public class Constants {
    // JWT
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    // Redis Key Prefix
    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    public static final String BLACKLIST_PREFIX = "blacklist:";

    // Error Messages
    public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    public static final String EXPIRED_TOKEN = "만료된 토큰입니다.";
    public static final String USER_ALREADY_EXISTS = "이미 존재하는 사용자입니다.";

    // Success Messages
    public static final String SIGNUP_SUCCESS = "회원가입이 완료되었습니다.";
    public static final String LOGIN_SUCCESS = "로그인이 완료되었습니다.";

    // Regex Patterns
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}
