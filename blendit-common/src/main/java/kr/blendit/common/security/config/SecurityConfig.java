package kr.blendit.common.security.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kr.blendit.common.security.config.SecurityProperties.ProtectedEndpoint;
import kr.blendit.common.security.config.SecurityProperties.PublicEndpoint;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
   * 인증 없이 접근 가능한 공개 엔드포인트 목록
   */
  public List<PublicEndpoint> getPublicEndpoints() {
    List<PublicEndpoint> endpoints = new ArrayList<>();

    // 기본 공개 경로 (Swagger, 개발용) - 모든 메서드 허용
    endpoints.add(new PublicEndpoint("/swagger-ui/**", List.of("*")));
    endpoints.add(new PublicEndpoint("/swagger-ui.html", List.of("*")));
    endpoints.add(new PublicEndpoint("/v3/api-docs/**", List.of("*")));
    endpoints.add(new PublicEndpoint("/docs/**", List.of("*")));
    endpoints.add(new PublicEndpoint("/h2-console/**", List.of("*")));
    endpoints.add(new PublicEndpoint("/actuator/**", List.of("*")));
    endpoints.add(new PublicEndpoint("/actuator/health", List.of("*")));

    // 추가 공개 경로 (application.yml에서 설정)
    endpoints.addAll(securityProperties.publicEndpoints());

    return Collections.unmodifiableList(endpoints);
  }

  /**
   * 반드시 인증이 필요한 보호된 엔드포인트 목록 (public 패턴에 매칭되더라도 인증 필요)
   */
  public List<ProtectedEndpoint> getProtectedEndpoints() {
    return securityProperties.protectedEndpoints();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    List<PublicEndpoint> publicEndpoints = getPublicEndpoints();
    List<ProtectedEndpoint> protectedEndpoints = getProtectedEndpoints();

    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> {
          // 각 public endpoint에 대해 메서드별로 permitAll 설정
          for (PublicEndpoint endpoint : publicEndpoints) {
            if (endpoint.methods().contains("*")) {
              auth.requestMatchers(endpoint.path()).permitAll();
            } else {
              for (String method : endpoint.methods()) {
                auth.requestMatchers(HttpMethod.valueOf(method), endpoint.path()).permitAll();
              }
            }
          }
          auth.anyRequest().authenticated();
        })
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter(publicEndpoints, protectedEndpoints), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private JwtAuthenticationFilter jwtAuthenticationFilter(List<PublicEndpoint> publicEndpoints,
      List<ProtectedEndpoint> protectedEndpoints) {
    List<String> processingPaths = List.of("/api/" + serviceName + "/**");
    return new JwtAuthenticationFilter(
        publicEndpoints,
        protectedEndpoints,
        processingPaths,
        failureHandler,
        jwtAuthenticationProvider
    );
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(List.of("Authorization", "refresh-token"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
