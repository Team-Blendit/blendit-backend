package kr.blendit.api.auth.repository;

import kr.blendit.api.auth.dto.oidc.OidcTokenResponse;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;

/**
 * OIDC 외부 API 호출을 위한 Repository 인터페이스
 * - 카카오, 구글 등 소셜 로그인 제공자별 구현체 생성
 */
public interface OidcRepository {

    /**
     * 인가 코드로 토큰 요청
     * @param code 인가 코드
     * @return 토큰 응답 (access_token, id_token 등)
     */
    OidcTokenResponse requestToken(String code);

    /**
     * ID Token 검증 및 사용자 정보 추출
     * @param idToken JWT 형태의 ID Token
     * @return 사용자 정보
     */
    OidcUserInfo verifyAndParseIdToken(String idToken);
}
