package kr.blendit.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.blendit.common.security.config.SecurityProperties.ProtectedEndpoint;
import kr.blendit.common.security.config.SecurityProperties.PublicEndpoint;
import kr.blendit.common.security.exception.JwtTokenErrorCode;
import kr.blendit.common.security.exception.JwtTokenException;
import kr.blendit.common.security.jwt.JwtAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationProvider provider;
    private final SkipPathRequestMatcher skipPathRequestMatcher;

    public JwtAuthenticationFilter(List<PublicEndpoint> publicEndpoints,
                                   List<ProtectedEndpoint> protectedEndpoints,
                                   List<String> processingPaths,
                                   AuthenticationFailureHandler failureHandler,
                                   AuthenticationProvider provider) {
        super(new SkipPathRequestMatcher(publicEndpoints, protectedEndpoints, processingPaths));
        this.skipPathRequestMatcher = new SkipPathRequestMatcher(publicEndpoints, protectedEndpoints, processingPaths);
        this.provider = provider;
        this.setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Public 엔드포인트인 경우 Optional Auth 처리
        if (skipPathRequestMatcher.isPublicEndpoint(request)) {
            tryOptionalAuthentication(request);
            chain.doFilter(request, response);
            return;
        }

        // 기본 필터 로직 수행
        super.doFilter(request, response, chain);
    }

    /**
     * Public 엔드포인트에서 토큰이 있으면 인증 시도 (실패해도 계속 진행)
     */
    private void tryOptionalAuthentication(HttpServletRequest request) {
        String tokenPayload = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isNull(tokenPayload) || !tokenPayload.startsWith("Bearer ")) {
            return;
        }

        try {
            String token = tokenPayload.replace("Bearer ", "");
            Authentication auth = provider.authenticate(new JwtAuthenticationToken(token));
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        } catch (Exception e) {
            log.debug("Optional auth failed for public endpoint: {}", e.getMessage());
            // Public 엔드포인트이므로 실패해도 무시하고 진행
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String tokenPayload = extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return provider.authenticate(new JwtAuthenticationToken(tokenPayload));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException authenticationException)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        getFailureHandler().onAuthenticationFailure(request, response, authenticationException);
    }

    @Override
    public void afterPropertiesSet() {
        // Skip AuthenticationManager check
    }

    private String extractToken(String tokenPayload) {
        if (isNull(tokenPayload)) {
            throw new JwtTokenException(JwtTokenErrorCode.TOKEN_NOT_FOUND);
        }
        if (!tokenPayload.startsWith("Bearer ")) {
            throw new JwtTokenException(JwtTokenErrorCode.INVALID_TOKEN);
        }
        return tokenPayload.replace("Bearer ", "");
    }
}
