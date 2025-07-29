package com.edumanager.common.validation;

public final class ValidationConstants {
    private ValidationConstants() {
        // 인스턴스 생성 방지
    }

    // 정규표현식 패턴
    public static final class Patterns {
        // 비밀번호: 8자 이상, 영문자, 숫자, 특수문자 각 1개 이상 포함
        public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";

        // 한국 휴대폰 번호: 010-1234-5678, 01012345678 형식 모두 허용
        public static final String PHONE_NUMBER = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$";

        // 이메일: 표준 이메일 형식
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";

        // 한글 이름: 2-50자의 한글, 영문, 공백만 허용
        public static final String NAME = "^[가-힣a-zA-Z\\s]{2,50}$";
    }

    // 검증 메시지
    public static final class Messages {
        // 공통
        public static final String REQUIRED = "필수 입력 항목입니다.";

        // 이메일
        public static final String EMAIL_REQUIRED = "이메일은 필수입니다.";
        public static final String EMAIL_INVALID = "올바른 이메일 형식이 아닙니다.";
        public static final String EMAIL_DUPLICATE = "이미 사용중인 이메일입니다.";

        // 비밀번호
        public static final String PASSWORD_REQUIRED = "비밀번호는 필수입니다.";
        public static final String PASSWORD_INVALID = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.";
        public static final String PASSWORD_CONFIRM_REQUIRED = "비밀번호 확인은 필수입니다.";
        public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";

        // 이름
        public static final String NAME_REQUIRED = "이름은 필수입니다.";
        public static final String NAME_INVALID = "이름은 2자 이상 50자 이하의 한글, 영문만 입력 가능합니다.";

        // 전화번호
        public static final String PHONE_INVALID = "올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)";

        // 역할
        public static final String ROLE_REQUIRED = "역할은 필수입니다.";
    }

    // 크기 제한
    public static final class Size {
        public static final int NAME_MIN = 2;
        public static final int NAME_MAX = 50;
        public static final int EMAIL_MAX = 100;
        public static final int PHONE_MAX = 20;
        public static final int PASSWORD_MIN = 8;
        public static final int PASSWORD_MAX = 100;
    }
}
