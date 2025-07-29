package com.edumanager.common.validation.validator;

import com.edumanager.common.validation.annotation.PasswordMatches;
import com.edumanager.domain.auth.dto.request.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof SignupRequest request) {
            return request.getPassword() != null
                    && request.getPassword().equals(request.getPasswordConfirm());
        }
        return true;
    }
}
