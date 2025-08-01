package com.edumanager.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.edumanager.common.constant.AppConstants;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="jwt")
public class JwtProperties {

    private String issuer = "edu-manager";
    private String privateKeyPath;
    private String publicKeyPath;
    private Duration accessTokenValidity = Duration.ofHours(24);
    private Duration refreshTokenValidity = Duration.ofDays(7);
    private String audience = "edu-manager-api";

    // JWT Claims
    private String authoritiesClaim = AppConstants.Jwt.CLAIM_AUTHORITIES;
    private String userIdClaim = AppConstants.Jwt.CLAIM_USER_ID;
    private String emailClaim = AppConstants.Jwt.CLAIM_EMAIL;
}
