package kr.blendit.api.auth.repository.dto;

import kr.blendit.api.auth.dto.oidc.OidcUserInfo;

/**
 * 구글 OIDC ID Token JWT 페이로드
 * - sub: 구글 사용자 고유 ID (socialCompanyUserId로 사용)
 * - name: 사용자 이름
 * - picture: 프로필 이미지 URL
 * - email: 이메일 주소
 */
public record GoogleIdTokenPayload(
        String iss,        // 발급자 (https://accounts.google.com)
        String aud,        // 대상 (client_id)
        String sub,        // 구글 사용자 ID
        String name,       // 이름
        String picture,    // 프로필 이미지
        String email       // 이메일
) implements OidcUserInfo {

    @Override
    public String socialCompanyUserId() {
        return sub;
    }

    @Override
    public String nickname() {
        return name;
    }

    @Override
    public String profileImage() {
        return picture;
    }

    @Override
    public String email() {
        return email;
    }
}
