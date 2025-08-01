package com.edumanager.common.validation.validator;

import com.edumanager.common.constant.AppConstants;
import com.edumanager.common.validation.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private Pattern pattern;
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.pattern=Pattern.compile(AppConstants.Validation.Pattern.PASSWORD);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if(password==null){
            return true;
        }
        return pattern.matcher(password).matches();
    }

}
