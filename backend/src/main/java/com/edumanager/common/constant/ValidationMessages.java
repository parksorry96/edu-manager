package com.edumanager.common.constant;

/**
 * 유효성 검증 메시지를 관리하는 상수 클래스
 * Bean Validation 어노테이션에서 사용
 */
public final class ValidationMessages {

    private ValidationMessages() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // 공통
    public static final String REQUIRED = "{validation.required}";
    public static final String INVALID_FORMAT = "{validation.invalid.format}";

    // 이메일
    public static final String EMAIL_REQUIRED = "{validation.email.required}";
    public static final String EMAIL_INVALID = "{validation.email.invalid}";
    public static final String EMAIL_DUPLICATE = "{validation.email.duplicate}";
    public static final String EMAIL_SIZE = "{validation.email.size}";

    // 비밀번호
    public static final String PASSWORD_REQUIRED = "{validation.password.required}";
    public static final String PASSWORD_INVALID = "{validation.password.invalid}";
    public static final String PASSWORD_SIZE = "{validation.password.size}";
    public static final String PASSWORD_CONFIRM_REQUIRED = "{validation.password.confirm.required}";
    public static final String PASSWORD_NOT_MATCH = "{validation.password.not.match}";

    // 이름
    public static final String NAME_REQUIRED = "{validation.name.required}";
    public static final String NAME_INVALID = "{validation.name.invalid}";
    public static final String NAME_SIZE = "{validation.name.size}";

    // 전화번호
    public static final String PHONE_INVALID = "{validation.phone.invalid}";
    public static final String PHONE_SIZE = "{validation.phone.size}";

    // 역할
    public static final String ROLE_REQUIRED = "{validation.role.required}";
    public static final String ROLE_INVALID = "{validation.role.invalid}";

    // 숫자
    public static final String POSITIVE = "{validation.positive}";
    public static final String POSITIVE_OR_ZERO = "{validation.positive.or.zero}";
    public static final String MIN = "{validation.min}";
    public static final String MAX = "{validation.max}";

    // 날짜
    public static final String PAST = "{validation.past}";
    public static final String PAST_OR_PRESENT = "{validation.past.or.present}";
    public static final String FUTURE = "{validation.future}";
    public static final String FUTURE_OR_PRESENT = "{validation.future.or.present}";
}
