package kr.blendit.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.blendit.common.response.CommonResponse;
import kr.blendit.common.security.exception.JwtTokenErrorCode;
import kr.blendit.common.security.exception.JwtTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        JwtTokenException jwtTokenException = (JwtTokenException) exception;
        JwtTokenErrorCode errorCode = (JwtTokenErrorCode) jwtTokenException.getErrorCode();

        int httpStatus = switch (errorCode) {
            case TOKEN_NOT_FOUND, EXPIRED_TOKEN, INVALID_TOKEN -> HttpStatus.UNAUTHORIZED.value();
            case FORBIDDEN_TOKEN -> HttpStatus.FORBIDDEN.value();
        };

        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), CommonResponse.fail(errorCode));
    }
}
