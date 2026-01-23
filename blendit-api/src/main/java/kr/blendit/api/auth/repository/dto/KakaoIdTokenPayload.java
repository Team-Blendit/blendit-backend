package kr.blendit.api.auth.repository.dto;

import kr.blendit.api.auth.dto.oidc.OidcUserInfo;

/**
 * 카카오 OIDC ID Token JWT 페이로드
 * - sub: 카카오 사용자 고유 ID (socialCompanyUserId로 사용)
 * - nickname: 사용자 닉네임
 * - picture: 프로필 이미지 URL
 */
public record KakaoIdTokenPayload(
        String iss,        // 발급자 (https://kauth.kakao.com)
        String aud,        // 대상 (client_id)
        String sub,        // 카카오 사용자 ID
        String nickname,   // 닉네임
        String picture     // 프로필 이미지
) implements OidcUserInfo {

    @Override
    public String socialCompanyUserId() {
        return sub;
    }

    @Override
    public String profileImage() {
        return picture;
    }
}
