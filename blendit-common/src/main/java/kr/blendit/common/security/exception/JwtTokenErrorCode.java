package kr.blendit.common.security.exception;

import kr.blendit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtTokenErrorCode implements ErrorCode {

    TOKEN_NOT_FOUND("토큰이 존재하지 않습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN("토큰이 만료되었습니다."),
    FORBIDDEN_TOKEN("유효하지 않은 권한의 토큰입니다.");

    private final String message;
}
