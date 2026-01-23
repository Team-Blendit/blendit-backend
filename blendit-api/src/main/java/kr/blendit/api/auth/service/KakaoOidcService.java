package kr.blendit.api.auth.service;

import kr.blendit.api.auth.dto.oidc.OidcTokenResponse;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.auth.repository.OidcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOidcService {

    private final OidcRepository kakaoOidcRepository;

    /**
     * 카카오 OIDC 인증 처리
     * 1. 인가 코드로 토큰 + ID Token 요청
     * 2. ID Token 검증 및 파싱
     *
     * @return 검증된 사용자 정보
     */
    public OidcUserInfo authenticate(String code) {
        // 1. 카카오 토큰 요청 (ID Token 포함)
        OidcTokenResponse tokenResponse = kakaoOidcRepository.requestToken(code);

        // 2. ID Token 검증 및 파싱
        return kakaoOidcRepository.verifyAndParseIdToken(tokenResponse.idToken());
    }
}
