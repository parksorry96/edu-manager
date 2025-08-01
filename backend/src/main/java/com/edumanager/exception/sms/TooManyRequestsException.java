package com.edumanager.exception.sms;

import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;

public class TooManyRequestsException extends BusinessException {
    public TooManyRequestsException(String message) {
        super(ErrorCode.TOO_MANY_REQUESTS,message);
    }
}
