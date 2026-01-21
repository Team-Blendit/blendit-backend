package kr.blendit.api.auth.repository;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.security.interfaces.RSAPublicKey;

@Repository
@Slf4j
public class KakaoOidcRepository implements OidcRepository {

    private final KakaoOidcProperties kakaoProperties;
    private final RestClient restClient;
    private final JwkProvider jwkProvider;

    public KakaoOidcRepository(
            KakaoOidcProperties kakaoProperties,
            RestClient restClient,
            @Qualifier("kakaoJwkProvider") JwkProvider jwkProvider
    ) {
        this.kakaoProperties = kakaoProperties;
        this.restClient = restClient;
        this.jwkProvider = jwkProvider;
    }

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

        } catch (RestClientResponseException e) {
            log.error("카카오 토큰 요청 실패 - status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BaseException(BaseErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
        } catch (Exception e) {
            log.error("카카오 토큰 요청 실패: {}", e.getMessage(), e);
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
            Jwk jwk = jwkProvider.get(kid);
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

}
