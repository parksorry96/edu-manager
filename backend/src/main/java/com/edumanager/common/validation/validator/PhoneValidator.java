package com.edumanager.common.validation.validator;

import com.edumanager.common.constant.AppConstants;
import com.edumanager.common.validation.annotation.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private Pattern pattern;
    private boolean required;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.pattern=Pattern.compile(AppConstants.Validation.Pattern.PHONE);
        this.required=constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if(!required &&(phone==null || phone.trim().isEmpty())){
            return true;
        }

        if(required &&(phone==null || phone.trim().isEmpty())){
            return false;
        }

        return pattern.matcher(phone).matches();
    }
}
