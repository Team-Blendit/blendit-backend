package kr.blendit.common.security.config;

import kr.blendit.common.security.filter.JwtAuthenticationFilter;
import kr.blendit.common.security.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler failureHandler;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final SecurityProperties securityProperties;

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 없이 접근 가능한 공개 경로
     */
    public List<String> getPublicPaths() {
        List<String> paths = new ArrayList<>();

        // 기본 공개 경로 (Swagger, 개발용)
        paths.add("/swagger-ui/**");
        paths.add("/swagger-ui.html");
        paths.add("/v3/api-docs/**");
        paths.add("/docs/**");
        paths.add("/h2-console/**");
        paths.add("/actuator/**");
        paths.add("/actuator/health");

        // 추가 공개 경로 (application.yml에서 설정)
        paths.addAll(securityProperties.publicPaths());

        return Collections.unmodifiableList(paths);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        List<String> publicPaths = getPublicPaths();

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths.toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(publicPaths), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter(List<String> publicPaths) {
        List<String> processingPaths = List.of("/api/" + serviceName + "/**");
        return new JwtAuthenticationFilter(
                publicPaths,
                processingPaths,
                failureHandler,
                jwtAuthenticationProvider
        );
    }
}
