package com.edumanager.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token은 필수입니다.")
    private String refreshToken;
}
