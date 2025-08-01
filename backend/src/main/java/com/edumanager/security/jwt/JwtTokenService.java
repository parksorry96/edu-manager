package com.edumanager.security.jwt;

import com.edumanager.common.constant.AppConstants;
//import static com.edumanager.common.constant.AppConstants.*;
import com.edumanager.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final RSAPrivateKey rsaPrivateKey;
    private final RSAPublicKey rsaPublicKey;

    public String createAccessToken(Authentication authentication, User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.getAccessTokenValidity());

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(authentication.getName())
                .audience(List.of(jwtProperties.getAudience()))
                .claim(jwtProperties.getAuthoritiesClaim(), authorities)
                .claim(jwtProperties.getUserIdClaim(), user.getId())
                .claim(jwtProperties.getEmailClaim(), user.getEmail())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String createRefreshToken(String username, Long userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.getRefreshTokenValidity());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .audience(List.of(jwtProperties.getAudience()))
                .claim(jwtProperties.getUserIdClaim(), userId)
                .claim(AppConstants.Jwt.CLAIM_TOKEN_TYPE, AppConstants.Jwt.TOKEN_TYPE_REFRESH)
                .build();

        String refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Redis에 저장
        stringRedisTemplate.opsForValue().set(
                AppConstants.Redis.PREFIX_REFRESH_TOKEN + username,
                refreshToken,
                jwtProperties.getRefreshTokenValidity().toMillis(),
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public boolean validateToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            // 블랙리스트 체크
            Boolean isBlacklisted = stringRedisTemplate.hasKey(AppConstants.Redis.PREFIX_BLACKLIST + token);
            return !isBlacklisted;

        } catch (JwtException e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public void blacklistToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            long expirationTime = jwt.getExpiresAt().toEpochMilli() - System.currentTimeMillis();

            if (expirationTime > 0) {
                stringRedisTemplate.opsForValue().set(
                        AppConstants.Redis.PREFIX_BLACKLIST + token,
                        "true",
                        expirationTime,
                        TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
        }
    }
}
