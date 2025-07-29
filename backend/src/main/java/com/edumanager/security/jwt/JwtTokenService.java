package com.edumanager.security.jwt;

import com.edumanager.common.constant.Constants;
import com.edumanager.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
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
    private final RedisTemplate<String, String>  redisTemplate;
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
                .claim("token_type", "refresh")
                .build();

        String refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Redis에 저장
        redisTemplate.opsForValue().set(
                Constants.REFRESH_TOKEN_PREFIX + username,
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
            Boolean isBlacklisted = redisTemplate.hasKey(Constants.BLACKLIST_PREFIX + token);
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
                redisTemplate.opsForValue().set(
                        Constants.BLACKLIST_PREFIX + token,
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
