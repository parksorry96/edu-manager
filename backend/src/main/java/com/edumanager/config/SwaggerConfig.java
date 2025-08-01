package com.edumanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 3.0 설정 클래스
 *
 * API 문서화를 위한 SpringDoc OpenAPI 설정을 정의합니다.
 * JWT 인증 스키마를 포함하여 보안이 필요한 API 테스트를 지원합니다.
 *
 * @author parkjisong
 * @since 1.0
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:Edu Manager}")
    private String applicationName;

    /**
     * OpenAPI 기본 설정
     *
     * API 문서의 기본 정보, 보안 스키마, 서버 정보 등을 설정합니다.
     * JWT Bearer 토큰 인증 방식을 전역으로 적용합니다.
     *
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // API 기본 정보 설정
        Info info = new Info()
                .title(applicationName + " API")
                .version("1.0.0")
                .description("학원 관리 시스템 REST API 문서")
                .contact(new Contact()
                        .name("박지송")
                        .email("admin@edumanager.com")
                        .url("https://edumanager.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        // 서버 정보 설정 (개발/운영 환경)
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Server prodServer = new Server()
                .url("https://api.edumanager.com")
                .description("Production Server");

        // JWT 보안 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer 토큰을 입력하세요. (Bearer 접두사는 자동으로 추가됩니다)");

        // 전역 보안 요구사항 설정 - 제거 (각 API에서 개별 설정)
        // SecurityRequirement securityRequirement = new SecurityRequirement()
        //         .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, prodServer))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme));
        // .addSecurityItem(securityRequirement);  // 전역 보안 제거
    }

    /**
     * OpenAPI Customizer
     *
     * 보안 요구사항을 경로별로 다르게 적용하기 위한 customizer
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                // 공개 API 경로는 보안 요구사항 제거
                if (path.startsWith("/api/auth/signup") ||
                        path.startsWith("/api/auth/login") ||
                        path.startsWith("/api/auth/refresh") ||
                        path.startsWith("/api/auth/check-email")) {
                    // 보안 요구사항 제거
                    pathItem.readOperations().forEach(operation -> {
                        operation.setSecurity(List.of());
                    });
                } else {
                    // 나머지는 JWT 보안 적용
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                    });
                }
            });
        };
    }

    /**
     * 공개 API 그룹 설정
     *
     * 인증이 필요 없는 공개 API들을 그룹화합니다.
     * (회원가입, 로그인, 토큰 갱신 등)
     *
     * @return 공개 API 그룹
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("1-public-api")  // 숫자를 앞에 붙여 정렬
                .displayName("공개 API")
                .pathsToMatch(
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/check-email"
                )
                .build();
    }

    /**
     * 인증 필요 API 그룹 설정
     *
     * JWT 토큰 인증이 필요한 API들을 그룹화합니다.
     *
     * @return 인증 필요 API 그룹
     */
    @Bean
    public GroupedOpenApi privateApi() {
        return GroupedOpenApi.builder()
                .group("2-private-api")  // 숫자를 앞에 붙여 정렬
                .displayName("인증 필요 API")
                .pathsToMatch("/api/**")
                .pathsToExclude(
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/check-email"
                )
                .build();
    }

    /**
     * 관리자 API 그룹 설정
     *
     * 관리자 권한이 필요한 API들을 그룹화합니다.
     *
     * @return 관리자 API 그룹
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("3-admin-api")  // 숫자를 앞에 붙여 정렬
                .displayName("관리자 API")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    /**
     * 전체 API 그룹 설정
     *
     * 모든 API를 포함하는 그룹입니다.
     *
     * @return 전체 API 그룹
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("0-all-api")  // 숫자를 앞에 붙여 정렬 (가장 먼저 표시)
                .displayName("전체 API")
                .pathsToMatch("/api/**")
                .build();
    }
}