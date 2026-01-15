package kr.blendit.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.blendit.common.security.exception.JwtTokenErrorCode;
import kr.blendit.common.security.exception.JwtTokenException;
import kr.blendit.common.security.jwt.JwtAuthenticationToken;
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

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationProvider provider;

    public JwtAuthenticationFilter(List<String> pathsToSkip, List<String> processingPaths,
                                   AuthenticationFailureHandler failureHandler,
                                   AuthenticationProvider provider) {
        super(new SkipPathRequestMatcher(pathsToSkip, processingPaths));
        this.provider = provider;
        this.setAuthenticationFailureHandler(failureHandler);
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
