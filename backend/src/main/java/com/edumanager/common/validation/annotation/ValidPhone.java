package com.edumanager.common.validation.annotation;

import com.edumanager.common.validation.ValidationConstants;
import com.edumanager.common.validation.validator.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface ValidPhone {
    String message() default ValidationConstants.Messages.PHONE_INVALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean required() default false; //
}
