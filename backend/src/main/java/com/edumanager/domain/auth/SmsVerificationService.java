package com.edumanager.domain.auth;

import com.edumanager.exception.sms.InvalidVerificationCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import static com.edumanager.common.constant.AppConstants.Sms.*;
import static com.edumanager.common.constant.AppConstants.Redis.*;

@Service
@RequiredArgsConstructor
public class SmsVerificationService {
    private final RedisTemplate<String,String> redisTemplate;


    public void sendVerificationCode(String phoneNumber){



    }


    private boolean verifyCode(String phoneNumber, String inputCode){
        String codeKey = PREFIX_SMS_CODE + phoneNumber;
        String saveCode = redisTemplate.opsForValue().get(codeKey);

        if(saveCode == null){
            throw new InvalidVerificationCodeException("인증번호가 만료되었습니다.");
        }
        return true;
    }

}
