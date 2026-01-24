package kr.blendit.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blendit API")
                        .description("Blendit API 문서")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 접두사 없이)")));
    }

    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("1. 인증")
                .displayName("인증 (Auth)")
                .pathsToMatch("/api/blendit/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("2. 사용자")
                .displayName("사용자 (User)")
                .pathsToMatch("/api/blendit/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("3. 관리자")
                .displayName("관리자 (Admin)")
                .pathsToMatch("/api/blendit/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi securedGroup() {
        return GroupedOpenApi.builder()
                .group("4. 보안 테스트")
                .displayName("보안 테스트 (Secured)")
                .pathsToMatch("/api/blendit/secured/**")
                .build();
    }

    @Bean
    public GroupedOpenApi blendingGroup() {
        return GroupedOpenApi.builder()
                .group("4. 블렌딩")
                .displayName("블렌딩 (Blending)")
                .pathsToMatch("/api/blendit/blending/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder()
                .group("0. 전체")
                .displayName("전체 API")
                .pathsToMatch("/api/blendit/**")
                .build();
    }
}
