package kr.blendit.api.auth.dto.oidc;

/**
 * OIDC 토큰 응답 공통 인터페이스
 */
public interface OidcTokenResponse {
    String accessToken();
    String idToken();
}
