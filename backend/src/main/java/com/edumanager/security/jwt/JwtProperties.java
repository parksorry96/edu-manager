package com.edumanager.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

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
    private String authoritiesClaim = "authorities";
    private String userIdClaim = "userId";
    private String emailClaim = "email";
}
