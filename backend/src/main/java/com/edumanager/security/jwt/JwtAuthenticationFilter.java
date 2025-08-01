package com.edumanager.security.jwt;

import static com.edumanager.common.constant.AppConstants.Jwt.*;

import com.edumanager.common.constant.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtTokenService jwtTokenService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> EXCLUED_PATHS= Arrays.asList(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/check-email"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        String requestPath = request.getRequestURI();
        
        if(shouldNotFilter(requestPath)){
            filterChain.doFilter(request,response);
            return;
        }

        // 로그아웃 요청인 경우 토큰이 있으면 검증하지만 실패해도 진행
        boolean isLogoutRequest = requestPath.equals("/api/auth/logout");
        
        if (StringUtils.hasText(token)) {
            if (jwtTokenService.validateToken(token)) {
                try {
                    Authentication authentication = jwtAuthenticationConverter.convert(jwtDecoder.decode(token));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 인증 성공: {}", authentication.getName());
                } catch (Exception e) {
                    log.error("JWT 인증 변환 실패: {}", e.getMessage());
                    if (!isLogoutRequest) {
                        log.debug("인증 실패로 인한 SecurityContext 미설정");
                    }
                }
            } else {
                log.debug("JWT 토큰 검증 실패 - 토큰이 유효하지 않음");
                if (isLogoutRequest) {
                    log.debug("로그아웃 요청이므로 토큰 검증 실패를 무시하고 진행");
                }
            }
        } else {
            log.debug("Authorization 헤더에 JWT 토큰이 없음");
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldNotFilter(String requestPath) {
        return EXCLUED_PATHS.stream()
                .anyMatch(pattern->pathMatcher.match(pattern,requestPath));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
