package kr.blendit.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final Throwable th;
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.th = null;
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable th) {
        super(message);
        this.th = th;
        this.errorCode = null;
    }
}
