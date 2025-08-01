package com.edumanager.exception.auth;

import com.edumanager.common.constant.AppConstants;
import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(String email){
        super(ErrorCode.DUPLICATE_EMAIL,String.format(AppConstants.Message.Error.EMAIL_ALREADY_EXISTS+": %s", email));
    }
}
