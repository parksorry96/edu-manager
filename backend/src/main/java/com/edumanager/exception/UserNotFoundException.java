package com.edumanager.exception;

import com.edumanager.exception.common.BusinessException;
import com.edumanager.exception.common.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(){
       super(ErrorCode.USER_NOT_FOUND);
   }

   public UserNotFoundException(String message){
        super(ErrorCode.USER_NOT_FOUND, message);
   }

    public static UserNotFoundException withId(Long userId) {
        return new UserNotFoundException(String.format("사용자를 찾을 수 없습니다. ID: %d", userId));
    }

    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException(String.format("사용자를 찾을 수 없습니다. Email: %s", email));
    }

}
