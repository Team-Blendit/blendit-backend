package kr.blendit.common.security.jwt;

import kr.blendit.common.constant.UserRole;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * JWT 토큰에서 추출한 현재 사용자 정보
 * UserEntity 의존성 없이 JWT 기반으로 동작
 */
@Getter
public class CurrentUser {

    private final String userUuid;
    private final List<UserRole> roles;

    public CurrentUser(String userUuid, List<UserRole> roles) {
        this.userUuid = Objects.requireNonNull(userUuid, "userUuid must not be null");
        this.roles = roles != null ? Collections.unmodifiableList(roles) : Collections.emptyList();
    }

    /**
     * 사용자가 특정 역할을 가지고 있는지 확인
     */
    public boolean hasRole(UserRole role) {
        if (role == null) {
            return false;
        }
        return roles.contains(role);
    }

    /**
     * 사용자가 지정된 역할 중 하나라도 가지고 있는지 확인
     */
    public boolean hasAnyRole(UserRole... checkRoles) {
        if (checkRoles == null || checkRoles.length == 0) {
            return false;
        }
        for (UserRole role : checkRoles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자가 관리자인지 확인
     */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }
}
