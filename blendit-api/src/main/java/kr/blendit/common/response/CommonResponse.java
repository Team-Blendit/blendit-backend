package kr.blendit.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.blendit.common.constant.ResultCode;
import kr.blendit.common.exception.ErrorCode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private String result;

    @Schema(description = "성공 시 응답 데이터")
    private T data;

    @Schema(description = "에러 응답 정보")
    private ErrorResponse error;

    @Schema(description = "응답 메시지")
    private String message;

    // === 성공 응답 ===

    public static <T> CommonResponse<T> success(T data, String message) {
        return CommonResponse.<T>builder()
                .result(ResultCode.SUCCESS.name())
                .data(data)
                .message(message)
                .build();
    }

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .result(ResultCode.SUCCESS.name())
                .data(data)
                .build();
    }

    public static CommonResponse<Void> success(String message) {
        return CommonResponse.<Void>builder()
                .result(ResultCode.SUCCESS.name())
                .message(message)
                .build();
    }

    public static CommonResponse<Void> success() {
        return CommonResponse.<Void>builder()
                .result(ResultCode.SUCCESS.name())
                .build();
    }

    // === 실패 응답 ===

    public static CommonResponse<Void> fail(List<String> messages) {
        return CommonResponse.<Void>builder()
                .result(ResultCode.FAILED.name())
                .error(ErrorResponse.of(messages))
                .build();
    }

    public static CommonResponse<Void> fail(String message) {
        return CommonResponse.<Void>builder()
                .result(ResultCode.FAILED.name())
                .error(ErrorResponse.of(message))
                .build();
    }

    public static CommonResponse<Void> fail(ErrorCode errorCode) {
        return CommonResponse.<Void>builder()
                .result(ResultCode.FAILED.name())
                .error(new ErrorResponse(errorCode))
                .build();
    }

    public static CommonResponse<Void> fail(String errorCode, String message) {
        return CommonResponse.<Void>builder()
                .result(ResultCode.FAILED.name())
                .error(new ErrorResponse(errorCode, message))
                .build();
    }
}
