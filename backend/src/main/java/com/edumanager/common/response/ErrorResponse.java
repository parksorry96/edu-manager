package com.edumanager.common.response;

import com.edumanager.exception.common.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RFC 9457 Problem Details 표준을 준수하는 에러 응답 클래스
 * application/problem+json 미디어 타입으로 반환
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9457">RFC 9457</a>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "title", "status", "detail", "instance", "timestamp", "errors"})
public class ErrorResponse {

    public static final MediaType PROBLEM_MEDIA_TYPE = MediaType.parseMediaType("application/problem+json");

    /**
     * 문제 유형을 식별하는 URI 참조
     * 예: "https://example.com/probs/out-of-credit"
     */
    @JsonProperty("type")
    private URI type;

    /**
     * 문제 유형에 대한 간단하고 사람이 읽을 수 있는 요약
     * 예: "You do not have enough credit."
     */
    @JsonProperty("title")
    private String title;

    /**
     * 이 문제 발생에 대한 HTTP 상태 코드
     */
    @JsonProperty("status")
    private int status;

    /**
     * 이 문제 발생에 대한 사람이 읽을 수 있는 설명
     * 예: "Your current balance is 30, but that costs 50."
     */
    @JsonProperty("detail")
    private String detail;

    /**
     * 이 문제 발생을 식별하는 URI 참조
     * 예: "/account/12345/msgs/abc"
     */
    @JsonProperty("instance")
    private URI instance;

    /**
     * 문제가 발생한 시간
     */
    @JsonProperty("timestamp")
    private Instant timestamp;

    /**
     * 검증 오류 등의 추가 오류 정보 (RFC 9457 확장)
     */
    @JsonProperty("errors")
    private List<ValidationError> errors;

    @Builder
    private ErrorResponse(URI type, String title, int status, String detail,
                          URI instance, List<ValidationError> errors) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    /**
     * ErrorCode로부터 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .type(URI.create("/errors/" + errorCode.getCode().toLowerCase()))
                .title(errorCode.getMessage())
                .status(errorCode.getHttpStatus().value())
                .detail(errorCode.getMessage())
                .build();
    }

    /**
     * ErrorCode와 상세 메시지로 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, String detail) {
        return ErrorResponse.builder()
                .type(URI.create("/errors/" + errorCode.getCode().toLowerCase()))
                .title(errorCode.getMessage())
                .status(errorCode.getHttpStatus().value())
                .detail(detail)
                .build();
    }

    /**
     * ErrorCode와 인스턴스 URI로 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, String detail, String instance) {
        return ErrorResponse.builder()
                .type(URI.create("/errors/" + errorCode.getCode().toLowerCase()))
                .title(errorCode.getMessage())
                .status(errorCode.getHttpStatus().value())
                .detail(detail)
                .instance(URI.create(instance))
                .build();
    }

    /**
     * 검증 오류를 포함한 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        List<ValidationError> validationErrors = bindingResult.getFieldErrors()
                .stream()
                .map(ValidationError::of)
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .type(URI.create("/errors/" + errorCode.getCode().toLowerCase()))
                .title(errorCode.getMessage())
                .status(errorCode.getHttpStatus().value())
                .detail("유효하지 않은 요청입니다. errors 필드를 확인해주세요.")
                .errors(validationErrors)
                .build();
    }

    /**
     * 직접 파라미터로 ErrorResponse 생성
     */
    public static ErrorResponse of(String type, String title, HttpStatus status, String detail) {
        return ErrorResponse.builder()
                .type(URI.create(type))
                .title(title)
                .status(status.value())
                .detail(detail)
                .build();
    }

    /**
     * 검증 오류 정보를 담는 내부 클래스
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonPropertyOrder({"field", "value", "reason"})
    public static class ValidationError {

        @JsonProperty("field")
        private String field;

        @JsonProperty("value")
        private Object value;

        @JsonProperty("reason")
        private String reason;

        @Builder
        private ValidationError(String field, Object value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static ValidationError of(FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .value(fieldError.getRejectedValue())
                    .reason(fieldError.getDefaultMessage())
                    .build();
        }

        public static ValidationError of(String field, Object value, String reason) {
            return ValidationError.builder()
                    .field(field)
                    .value(value)
                    .reason(reason)
                    .build();
        }
    }
}
