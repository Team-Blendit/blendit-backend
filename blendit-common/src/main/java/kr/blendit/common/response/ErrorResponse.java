package kr.blendit.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.blendit.common.exception.ErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String errorCode;
    private String message;
    private List<String> details;

    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(String errorCode, String message, List<String> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    // === 정적 팩토리 메서드 ===

    public static ErrorResponse of(String message) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(message);
        return response;
    }

    public static ErrorResponse of(List<String> messages) {
        ErrorResponse response = new ErrorResponse();
        response.setDetails(messages);
        if (!messages.isEmpty()) {
            response.setMessage(messages.get(0));
        }
        return response;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }
}
