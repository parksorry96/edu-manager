package com.edumanager.domain.auth;

import static com.edumanager.common.constant.AppConstants.*;

import com.edumanager.common.constant.AppConstants;
import com.edumanager.domain.auth.dto.request.LoginRequest;
import com.edumanager.domain.auth.dto.request.SignupRequest;
import com.edumanager.domain.auth.dto.response.LoginResponse;
import com.edumanager.domain.auth.dto.response.SignupResponse;
import com.edumanager.domain.auth.dto.response.TokenResponse;
import com.edumanager.domain.user.entity.User;
import com.edumanager.domain.user.repository.UserRepository;
import com.edumanager.exception.DuplicateEmailException;
import com.edumanager.exception.UserNotFoundException;
import com.edumanager.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        User savedUser = userRepository.save(user);

        log.info("유저 회원가입:{}, 권한 :{}", savedUser.getEmail(), savedUser.getRole());

        return SignupResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole())
                .message(Message.Success.SIGNUP)
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        Authentication authWithRole = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String accessToken = jwtTokenService.createAccessToken(authWithRole, user);
        String refreshToken = jwtTokenService.createRefreshToken(user.getEmail(), user.getId());

        log.info("유저 로그인 {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(Jwt.TOKEN_TYPE_BEARER)
                .expiresIn(Jwt.ACCESS_TOKEN_EXPIRE_SECONDS)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole())
                        .build())
                .message(Message.Success.LOGIN)
                .build();
    }

    public TokenResponse refreshToken(String refreshToken) {
        String username = jwtTokenService.getUsernameFromToken(refreshToken);
        String storedToken= redisTemplate.opsForValue().get(Redis.PREFIX_REFRESH_TOKEN + username);

        if(storedToken==null || !storedToken.equals(refreshToken)){
            throw new RuntimeException(Message.Error.INVALID_TOKEN);
        }

        User user = userRepository.findByEmailAndIsActiveTrue(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,null,Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String newAccessToken = jwtTokenService.createAccessToken(auth, user);
        String newRefreshToken = jwtTokenService.createRefreshToken(user.getEmail(), user.getId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(Jwt.TOKEN_TYPE_BEARER)
                .expiresIn(Jwt.ACCESS_TOKEN_EXPIRE_SECONDS)
                .build();
    }

    @Transactional
    public void logout(String token) {
        jwtTokenService.blacklistToken(token);

        String username=jwtTokenService.getUsernameFromToken(token);
        redisTemplate.delete(Redis.PREFIX_REFRESH_TOKEN + username);

        log.info("유저 로그아웃:{}", username);
    }
}
