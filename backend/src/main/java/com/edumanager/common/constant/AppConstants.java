package com.edumanager.common.constant;

public final class AppConstants {

    private AppConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // ======================== JWT 관련 상수 ========================
    public static final class Jwt {
        // HTTP 헤더
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String TOKEN_TYPE_BEARER = "Bearer";

        // 토큰 타입
        public static final String TOKEN_TYPE_ACCESS = "access";
        public static final String TOKEN_TYPE_REFRESH = "refresh";

        // 토큰 응답 필드
        public static final String ACCESS_TOKEN_FIELD = "access_token";
        public static final String REFRESH_TOKEN_FIELD = "refresh_token";
        public static final String TOKEN_TYPE_FIELD = "token_type";
        public static final String EXPIRES_IN_FIELD = "expires_in";

        // JWT Claims
        public static final String CLAIM_AUTHORITIES = "authorities";
        public static final String CLAIM_USER_ID = "userId";
        public static final String CLAIM_EMAIL = "email";
        public static final String CLAIM_TOKEN_TYPE = "token_type";

        // 토큰 만료 시간 (초 단위)
        public static final long ACCESS_TOKEN_EXPIRE_SECONDS = 86400L; // 24시간
        public static final long REFRESH_TOKEN_EXPIRE_SECONDS = 604800L; // 7일

        private Jwt() {}
    }

    // ======================== Redis 관련 상수 ========================
    public static final class Redis {
        // Key Prefix
        public static final String PREFIX_REFRESH_TOKEN = "refresh_token:";
        public static final String PREFIX_BLACKLIST = "blacklist:";
        public static final String PREFIX_EMAIL_VERIFICATION = "email_verify:";
        public static final String PREFIX_PASSWORD_RESET = "password_reset:";
        public static final String PREFIX_LOGIN_ATTEMPT = "login_attempt:";

        // TTL (초 단위)
        public static final long EMAIL_VERIFICATION_TTL = 3600L; // 1시간
        public static final long PASSWORD_RESET_TTL = 1800L; // 30분
        public static final long LOGIN_ATTEMPT_TTL = 3600L; // 1시간

        private Redis() {}
    }

    // ======================== 유효성 검증 상수 ========================
    public static final class Validation {

        // 정규표현식 패턴
        public static final class Pattern {
            // 이메일: 표준 이메일 형식
            public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

            // 비밀번호: 8자 이상, 영문 대소문자, 숫자, 특수문자 각 1개 이상
            public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";

            // 한국 휴대폰 번호: 010-1234-5678, 01012345678 형식 모두 허용
            public static final String PHONE = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$";

            // 이름: 2-50자의 한글, 영문, 공백만 허용
            public static final String NAME = "^[가-힣a-zA-Z\\s]{2,50}$";

            // 학번/사번: 숫자와 하이픈만 허용
            public static final String ID_NUMBER = "^[0-9-]{5,20}$";

            private Pattern() {}
        }

        // 크기 제한
        public static final class Size {
            // 이름
            public static final int NAME_MIN = 2;
            public static final int NAME_MAX = 50;

            // 이메일
            public static final int EMAIL_MIN = 5;
            public static final int EMAIL_MAX = 100;

            // 비밀번호
            public static final int PASSWORD_MIN = 8;
            public static final int PASSWORD_MAX = 100;

            // 전화번호
            public static final int PHONE_MIN = 10;
            public static final int PHONE_MAX = 20;

            // 주소
            public static final int ADDRESS_MAX = 200;

            // 설명/내용
            public static final int DESCRIPTION_MAX = 1000;
            public static final int CONTENT_MAX = 5000;

            private Size() {}
        }

        private Validation() {}
    }

    // ======================== API 응답 메시지 ========================
    public static final class Message {

        // 성공 메시지
        public static final class Success {
            public static final String SIGNUP = "회원가입이 완료되었습니다.";
            public static final String LOGIN = "로그인이 완료되었습니다.";
            public static final String LOGOUT = "로그아웃이 완료되었습니다.";
            public static final String TOKEN_REFRESH = "토큰이 갱신되었습니다.";
            public static final String PASSWORD_CHANGE = "비밀번호가 변경되었습니다.";
            public static final String USER_UPDATE = "사용자 정보가 수정되었습니다.";
            public static final String USER_DELETE = "사용자가 삭제되었습니다.";

            private Success() {}
        }

        // 에러 메시지
        public static final class Error {
            // 인증 관련
            public static final String UNAUTHORIZED = "인증이 필요합니다.";
            public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다.";
            public static final String EXPIRED_TOKEN = "만료된 토큰입니다.";
            public static final String ACCESS_DENIED = "접근 권한이 없습니다.";

            // 사용자 관련
            public static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
            public static final String EMAIL_ALREADY_EXISTS = "이미 사용중인 이메일입니다.";
            public static final String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";
            public static final String ACCOUNT_DISABLED = "비활성화된 계정입니다.";
            public static final String ACCOUNT_LOCKED = "잠긴 계정입니다. 관리자에게 문의하세요.";

            // 유효성 검증
            public static final String REQUIRED_FIELD = "필수 입력 항목입니다.";
            public static final String INVALID_FORMAT = "올바른 형식이 아닙니다.";
            public static final String INVALID_EMAIL = "올바른 이메일 형식이 아닙니다.";
            public static final String INVALID_PHONE = "올바른 휴대폰 번호 형식이 아닙니다.";
            public static final String INVALID_NAME = "이름은 2자 이상 50자 이하의 한글, 영문만 입력 가능합니다.";
            public static final String INVALID_PASSWORD_FORMAT = "비밀번호는 8자 이상, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.";
            public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";

            // 시스템 에러
            public static final String INTERNAL_SERVER_ERROR = "서버 오류가 발생했습니다.";
            public static final String SERVICE_UNAVAILABLE = "서비스를 일시적으로 사용할 수 없습니다.";

            private Error() {}
        }

        private Message() {}
    }

    // ======================== API 경로 ========================
    public static final class Api {
        public static final String BASE_PATH = "/api";

        // 인증 관련
        public static final String AUTH_BASE = BASE_PATH + "/auth";
        public static final String AUTH_SIGNUP = AUTH_BASE + "/signup";
        public static final String AUTH_LOGIN = AUTH_BASE + "/login";
        public static final String AUTH_LOGOUT = AUTH_BASE + "/logout";
        public static final String AUTH_REFRESH = AUTH_BASE + "/refresh";
        public static final String AUTH_VERIFY_EMAIL = AUTH_BASE + "/verify-email";
        public static final String AUTH_RESET_PASSWORD = AUTH_BASE + "/reset-password";

        // 사용자 관련
        public static final String USER_BASE = BASE_PATH + "/users";
        public static final String USER_PROFILE = USER_BASE + "/profile";
        public static final String USER_BY_ID = USER_BASE + "/{id}";

        // 관리자 관련
        public static final String ADMIN_BASE = BASE_PATH + "/admin";

        // Swagger 관련
        public static final String[] SWAGGER_PATHS = {
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
        };

        // 공개 접근 가능 경로
        public static final String[] PUBLIC_PATHS = {
                AUTH_SIGNUP,
                AUTH_LOGIN,
                AUTH_REFRESH,
                AUTH_VERIFY_EMAIL,
                AUTH_RESET_PASSWORD
        };

        private Api() {}
    }

    // ======================== 보안 관련 상수 ========================
    public static final class Security {
        // 권한 접두사
        public static final String ROLE_PREFIX = "ROLE_";

        // 기본 역할
        public static final String ROLE_ADMIN = "ADMIN";
        public static final String ROLE_TEACHER = "TEACHER";
        public static final String ROLE_STUDENT = "STUDENT";
        public static final String ROLE_PARENT = "PARENT";

        // 로그인 시도 제한
        public static final int MAX_LOGIN_ATTEMPTS = 5;
        public static final long LOGIN_ATTEMPT_BLOCK_DURATION = 1800L; // 30분

        private Security() {}
    }

    // ======================== 페이징 관련 상수 ========================
    public static final class Paging {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final int DEFAULT_PAGE_NUMBER = 0;

        public static final String PARAM_PAGE = "page";
        public static final String PARAM_SIZE = "size";
        public static final String PARAM_SORT = "sort";

        private Paging() {}
    }

    // ======================== 날짜 형식 상수 ========================
    public static final class DateFormat {
        public static final String DATE = "yyyy-MM-dd";
        public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME = "HH:mm:ss";
        public static final String YEAR_MONTH = "yyyy-MM";

        private DateFormat() {}
    }
}



