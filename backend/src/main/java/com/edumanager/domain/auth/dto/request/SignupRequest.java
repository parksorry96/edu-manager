package com.edumanager.domain.auth.dto.request;

import com.edumanager.common.constant.AppConstants;
import com.edumanager.common.constant.ValidationMessages;
import com.edumanager.common.validation.annotation.PasswordMatches;
import com.edumanager.common.validation.annotation.ValidPassword;
import com.edumanager.common.validation.annotation.ValidPhone;
import com.edumanager.domain.user.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@PasswordMatches(message = ValidationMessages.PASSWORD_NOT_MATCH)
public class SignupRequest {
    
    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = AppConstants.Validation.Size.EMAIL_MAX, 
          message = ValidationMessages.EMAIL_SIZE)
    private String email;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @ValidPassword(message = ValidationMessages.PASSWORD_INVALID)
    @Size(min = AppConstants.Validation.Size.PASSWORD_MIN,
          max = AppConstants.Validation.Size.PASSWORD_MAX,
          message = ValidationMessages.PASSWORD_SIZE)
    private String password;

    @NotBlank(message = ValidationMessages.PASSWORD_CONFIRM_REQUIRED)
    private String passwordConfirm;

    @NotBlank(message = ValidationMessages.NAME_REQUIRED)
    @Pattern(regexp = AppConstants.Validation.Pattern.NAME,
            message = ValidationMessages.NAME_INVALID)
    @Size(min = AppConstants.Validation.Size.NAME_MIN,
          max = AppConstants.Validation.Size.NAME_MAX,
          message = ValidationMessages.NAME_SIZE)
    private String name;

    @ValidPhone(message = ValidationMessages.PHONE_INVALID)
    @Size(max = AppConstants.Validation.Size.PHONE_MAX,
          message = ValidationMessages.PHONE_SIZE)
    private String phone;

    @NotNull(message = ValidationMessages.ROLE_REQUIRED)
    private UserRole role;
}
