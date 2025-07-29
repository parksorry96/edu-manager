package com.edumanager.exception;

import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(String email){
        super(ErrorCode.DUPLICATE_EMAIL,String.format("이미 사용중인 이메일입니다: %s", email));
    }
}
