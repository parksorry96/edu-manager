package com.edumanager.controller;

import com.edumanager.common.constant.AppConstants;
import com.edumanager.common.response.ApiResponse;
import com.edumanager.domain.auth.AuthService;

import static com.edumanager.common.constant.AppConstants.Api.*;
import static com.edumanager.common.constant.AppConstants.Jwt.BEARER_PREFIX;
import static com.edumanager.common.constant.AppConstants.Message.Success.*;

import com.edumanager.domain.auth.dto.request.LoginRequest;
import com.edumanager.domain.auth.dto.request.RefreshTokenRequest;
import com.edumanager.domain.auth.dto.request.SignupRequest;
import com.edumanager.domain.auth.dto.response.LoginResponse;
import com.edumanager.domain.auth.dto.response.SignupResponse;
import com.edumanager.domain.auth.dto.response.TokenResponse;
import com.edumanager.exception.common.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@Slf4j
@RequestMapping(AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = SignupResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이메일 중복"
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        log.debug("회원가입 요청: email={}, name={}, role={}",
                request.getEmail(), request.getName(), request.getRole());

        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response,SIGNUP));

    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인하여 액세스 토큰과 리프레시 토큰을 발급받습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (이메일 또는 비밀번호 불일치)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "계정이 비활성화됨"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        log.debug("로그인 요청: email={}", request.getEmail());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(response, LOGIN));
    }

    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        log.debug("토큰 갱신 요청");

        TokenResponse response = authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(response, TOKEN_REFRESH));
    }

    @Operation(
            summary = "로그아웃",
            description = "현재 토큰을 무효화하고 로그아웃합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("로그아웃 요청: username={}", userDetails.getUsername());

        // "Bearer " 접두사 제거
        String token = authHeader.substring(BEARER_PREFIX.length());
        authService.logout(token);

        return ResponseEntity.ok(ApiResponse.success(LOGOUT));
    }

    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 요청"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getCurrentUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("현재 사용자 정보 조회: username={}", userDetails.getUsername());

        // TODO: UserService를 통해 상세 사용자 정보 조회 구현
        // 현재는 간단히 username만 반환
        return ResponseEntity.ok(
                ApiResponse.success(userDetails.getUsername(), "사용자 정보 조회 성공")
        );
    }





    @Operation(
            summary = "이메일 중복 확인",
            description = "회원가입 전 이메일 중복 여부를 확인합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "확인 완료"
            )
    })
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(
            @RequestParam String email) {

        log.debug("이메일 중복 확인: email={}", email);

        // TODO: AuthService에 이메일 중복 확인 메서드 추가 필요
        // boolean isAvailable = authService.isEmailAvailable(email);

        return ResponseEntity.ok(
                ApiResponse.success(true, "사용 가능한 이메일입니다.")
        );
    }


}
