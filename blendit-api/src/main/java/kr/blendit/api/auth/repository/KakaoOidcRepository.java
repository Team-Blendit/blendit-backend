package kr.blendit.api.auth.repository;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.blendit.api.auth.config.KakaoOidcProperties;
import kr.blendit.api.auth.dto.oidc.OidcTokenResponse;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.auth.repository.dto.KakaoIdTokenPayload;
import kr.blendit.api.auth.repository.dto.KakaoTokenResponse;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class KakaoOidcRepository implements OidcRepository {

    private final KakaoOidcProperties kakaoProperties;
    private final RestClient restClient;
    private JwkProvider jwkProvider;

    @Override
    public OidcTokenResponse requestToken(String code) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", kakaoProperties.clientId());
            formData.add("client_secret", kakaoProperties.clientSecret());
            formData.add("redirect_uri", kakaoProperties.redirectUri());
            formData.add("code", code);

            return restClient.post()
                    .uri(kakaoProperties.tokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(KakaoTokenResponse.class);

        } catch (Exception e) {
            log.error("카카오 토큰 요청 실패: {}", e.getMessage());
            throw new BaseException(BaseErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
        }
    }

    @Override
    public OidcUserInfo verifyAndParseIdToken(String idToken) {
        try {
            // JWT 헤더에서 kid 추출
            DecodedJWT decodedJwt = JWT.decode(idToken);
            String kid = decodedJwt.getKeyId();

            // JWKS에서 공개키 조회
            JwkProvider provider = getJwkProvider();
            Jwk jwk = provider.get(kid);
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            // 서명 검증 및 클레임 검증
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            DecodedJWT verifiedJwt = JWT.require(algorithm)
                    .withIssuer(kakaoProperties.issuer())
                    .withAudience(kakaoProperties.clientId())
                    .build()
                    .verify(idToken);

            // 페이로드 추출
            return new KakaoIdTokenPayload(
                    verifiedJwt.getIssuer(),
                    verifiedJwt.getAudience().get(0),
                    verifiedJwt.getSubject(),
                    verifiedJwt.getClaim("nickname").asString(),
                    verifiedJwt.getClaim("picture").asString()
            );

        } catch (TokenExpiredException e) {
            log.error("ID Token 만료: {}", e.getMessage());
            throw new BaseException(BaseErrorCode.OAUTH_ID_TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.error("ID Token 검증 실패: {}", e.getMessage());
            throw new BaseException(BaseErrorCode.OAUTH_INVALID_ID_TOKEN);
        } catch (Exception e) {
            log.error("ID Token 파싱 실패: {}", e.getMessage());
            throw new BaseException(BaseErrorCode.OAUTH_INVALID_ID_TOKEN);
        }
    }

    /**
     * JWKS Provider (공개키 캐싱)
     */
    private JwkProvider getJwkProvider() {
        if (jwkProvider == null) {
            try {
                jwkProvider = new JwkProviderBuilder(new URL(kakaoProperties.jwksUri()))
                        .cached(10, 24, TimeUnit.HOURS)  // 최대 10개 키, 24시간 캐싱
                        .rateLimited(10, 1, TimeUnit.MINUTES)  // 분당 최대 10회 요청
                        .build();
            } catch (Exception e) {
                log.error("JWKS Provider 초기화 실패: {}", e.getMessage());
                throw new BaseException(BaseErrorCode.OAUTH_JWKS_FETCH_FAILED);
            }
        }
        return jwkProvider;
    }
}
