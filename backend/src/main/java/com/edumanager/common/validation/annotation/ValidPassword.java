package com.edumanager.common.validation.annotation;

import com.edumanager.common.validation.ValidationConstants;
import com.edumanager.common.validation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidPassword {
    String message() default ValidationConstants.Messages.PASSWORD_INVALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
