package com.edumanager.exception.sms;

import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;

public class PhoneNotVerifiedException extends BusinessException {
    public PhoneNotVerifiedException(String message) {
        super(ErrorCode.PHONE_NOT_VERIFIED, message);
    }
}
