package com.edumanager.exception.sms;

import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;

public class InvalidVerificationCodeException extends BusinessException {
    public InvalidVerificationCodeException(String message) {
        super(ErrorCode.INVALID_VERIFICATION_CODE, message);
    }
}
