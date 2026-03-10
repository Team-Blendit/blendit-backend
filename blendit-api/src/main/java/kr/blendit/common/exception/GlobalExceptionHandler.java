package kr.blendit.common.exception;

import kr.blendit.common.response.CommonResponse;
import kr.blendit.common.security.exception.JwtTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // === 4xx 클라이언트 에러 ===

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<CommonResponse<Void>> handleNotFound(Exception e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail("NOT_FOUND", "존재하지 않는 URL 입니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed: {} - {}", e.getMethod(), e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> messages = bindingResult.getFieldErrors().stream()
                .map(fieldError -> "[" + fieldError.getField() + "](은)는 "
                        + fieldError.getDefaultMessage()
                        + " 입력된 값: [" + fieldError.getRejectedValue() + "]")
                .toList();

        log.warn("Validation failed: {}", messages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail(messages));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("Request body not readable: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail("BAD_REQUEST", "Request Body가 존재하지 않거나 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<CommonResponse<Void>> handleJwtTokenException(JwtTokenException e) {
        log.warn("JWT token error: {}", e.getErrorCode().name());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail(e.getErrorCode()));
    }

    // === 비즈니스 예외 ===

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<Void>> handleBaseException(BaseException e) {
        if (e.getTh() != null) {
            log.error("Business exception with cause: {} - {}", e.getErrorCode().name(), e.getTh().getMessage(), e.getTh());
        } else {
            log.warn("Business exception: {} - {}", e.getErrorCode().name(), e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail(e.getErrorCode()));
    }

    // === 5xx 서버 에러 ===

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<CommonResponse<Void>> handleUnexpected(Throwable th) {
        log.error("Unexpected server error: {}", th.getMessage(), th);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(CommonResponse.fail("INTERNAL_SERVER_ERROR", "예상치 못한 서버 오류가 발생했습니다."));
    }
}
