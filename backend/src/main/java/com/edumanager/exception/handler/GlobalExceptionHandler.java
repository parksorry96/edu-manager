package com.edumanager.exception.handler;


import com.edumanager.common.response.ErrorResponse;
import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



/**
 * 전역 예외 처리기
 * RFC 9457 Problem Details 표준에 따라 에러 응답을 반환
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        log.error("BusinessException: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .header(HttpHeaders.CONTENT_TYPE, ErrorResponse.PROBLEM_MEDIA_TYPE.toString())
                .body(response);
    }

    /**
     * @Valid, @Validated 검증 실패 시 발생하는 예외 처리
     */

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE,
                ex.getBindingResult()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, ErrorResponse.PROBLEM_MEDIA_TYPE.toString())
                .body(response);
    }

    /**
     * @ModelAttribute 검증 실패 시 발생하는 예외 처리
     */

    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.error("BindException: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE,
                ex.getBindingResult()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, ErrorResponse.PROBLEM_MEDIA_TYPE.toString())
                .body(response);
    }

    /**
     * 타입 변환 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());

        String detail = String.format("'%s' 파라미터의 값 '%s'이(가) 올바르지 않습니다. %s 타입이어야 합니다.",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_TYPE_VALUE,
                detail,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, ErrorResponse.PROBLEM_MEDIA_TYPE.toString())
                .body(response);
    }

    /**
     * 처리되지 않은 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("Exception: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "예기치 않은 오류가 발생했습니다.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, ErrorResponse.PROBLEM_MEDIA_TYPE.toString())
                .body(response);
    }
}
