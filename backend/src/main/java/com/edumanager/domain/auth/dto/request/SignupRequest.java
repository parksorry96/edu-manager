package com.edumanager.domain.auth.dto.request;

import com.edumanager.common.validation.ValidationConstants;
import com.edumanager.common.validation.annotation.PasswordMatches;
import com.edumanager.common.validation.annotation.ValidPassword;
import com.edumanager.common.validation.annotation.ValidPhone;
import com.edumanager.domain.user.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@PasswordMatches
public class SignupRequest {
    @NotBlank(message = ValidationConstants.Messages.EMAIL_REQUIRED)
    @Email(message = ValidationConstants.Messages.EMAIL_INVALID)
    @Size(max = ValidationConstants.Size.EMAIL_MAX)
    private String email;

    @NotBlank(message = ValidationConstants.Messages.PASSWORD_REQUIRED)
    @ValidPassword // 커스텀 비밀번호 검증
    @Size(min = ValidationConstants.Size.PASSWORD_MIN,
            max = ValidationConstants.Size.PASSWORD_MAX)
    private String password;

    @NotBlank(message = ValidationConstants.Messages.PASSWORD_CONFIRM_REQUIRED)
    private String passwordConfirm;

    @NotBlank(message = ValidationConstants.Messages.NAME_REQUIRED)
    @Pattern(regexp = ValidationConstants.Patterns.NAME,
            message = ValidationConstants.Messages.NAME_INVALID)
    @Size(min = ValidationConstants.Size.NAME_MIN,
            max = ValidationConstants.Size.NAME_MAX)
    private String name;

    @ValidPhone // 커스텀 전화번호 검증 (선택사항)
    @Size(max = ValidationConstants.Size.PHONE_MAX)
    private String phone;

    @NotNull(message = ValidationConstants.Messages.ROLE_REQUIRED)
    private UserRole role;

}
