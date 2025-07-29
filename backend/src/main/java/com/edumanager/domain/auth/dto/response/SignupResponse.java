package com.edumanager.domain.auth.dto.response;

import com.edumanager.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class SignupResponse {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
    private String message;
}
