package kr.blendit.common.constant;

import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    public static UserRole of(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        throw new BaseException(BaseErrorCode.INVALID_USER_ROLE);
    }
}
