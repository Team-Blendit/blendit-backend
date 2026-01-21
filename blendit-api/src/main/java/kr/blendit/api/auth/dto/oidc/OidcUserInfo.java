package kr.blendit.api.auth.dto.oidc;

/**
 * OIDC 사용자 정보 공통 인터페이스
 */
public interface OidcUserInfo {
    String socialCompanyUserId();  // 소셜 제공자의 사용자 ID
    String nickname();
    String profileImage();
    default String email() {
        return null;
    }
}
