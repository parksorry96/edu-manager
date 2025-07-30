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

        if (StringUtils.hasText(token) && jwtTokenService.validateToken(token)) {
            try {
                Authentication authentication = jwtAuthenticationConverter.convert(jwtDecoder.decode(token));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("Could not set user authentication in security context", e);
            }
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
