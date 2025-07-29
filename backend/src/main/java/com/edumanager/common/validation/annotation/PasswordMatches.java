package com.edumanager.common.validation.annotation;

import com.edumanager.common.validation.ValidationConstants;
import com.edumanager.common.validation.validator.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default ValidationConstants.Messages.PASSWORD_NOT_MATCH;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
