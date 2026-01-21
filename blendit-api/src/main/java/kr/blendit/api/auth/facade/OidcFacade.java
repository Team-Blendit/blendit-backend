package kr.blendit.api.auth.facade;

import kr.blendit.api.auth.dto.OidcLoginResponse;
import kr.blendit.api.auth.dto.TokenDto;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.auth.service.GoogleOidcService;
import kr.blendit.api.auth.service.KakaoOidcService;
import kr.blendit.api.auth.service.TokenService;
import kr.blendit.api.user.domain.LoginType;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OidcFacade {

    private final KakaoOidcService kakaoOidcService;
    private final GoogleOidcService googleOidcService;
    private final UserService userService;
    private final TokenService tokenService;

    /**
     * 카카오 로그인 전체 플로우
     * 1. 카카오 OIDC 인증 (토큰 요청 + ID Token 검증)
     * 2. 사용자 조회/생성
     * 3. JWT 토큰 발급
     * 4. 응답 생성
     */
    @Transactional
    public OidcLoginResponse kakaoLogin(String code) {
        OidcUserInfo userInfo = kakaoOidcService.authenticate(code);
        User user = userService.findOrCreateBySocialLogin(LoginType.KAKAO, userInfo);
        TokenDto tokenDto = tokenService.issueTokenPair(user.getUuid());

        return OidcLoginResponse.of(tokenDto, user);
    }

    /**
     * 구글 로그인 전체 플로우
     * 1. 구글 OIDC 인증 (토큰 요청 + ID Token 검증)
     * 2. 사용자 조회/생성
     * 3. JWT 토큰 발급
     * 4. 응답 생성
     */
    @Transactional
    public OidcLoginResponse googleLogin(String code) {
        OidcUserInfo userInfo = googleOidcService.authenticate(code);
        User user = userService.findOrCreateBySocialLogin(LoginType.GOOGLE, userInfo);
        TokenDto tokenDto = tokenService.issueTokenPair(user.getUuid());

        return OidcLoginResponse.of(tokenDto, user);
    }
}
