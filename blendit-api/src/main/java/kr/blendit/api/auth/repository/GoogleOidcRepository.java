package kr.blendit.api.auth.repository;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.blendit.api.auth.config.GoogleOidcProperties;
import kr.blendit.api.auth.dto.oidc.OidcTokenResponse;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.auth.repository.dto.GoogleIdTokenPayload;
import kr.blendit.api.auth.repository.dto.GoogleTokenResponse;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
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
public class GoogleOidcRepository implements OidcRepository {

    private final GoogleOidcProperties googleProperties;
    private final RestClient restClient;
    private final JwkProvider jwkProvider;

    public GoogleOidcRepository(
            GoogleOidcProperties googleProperties,
            RestClient restClient,
            @Qualifier("googleJwkProvider") JwkProvider jwkProvider
    ) {
        this.googleProperties = googleProperties;
        this.restClient = restClient;
        this.jwkProvider = jwkProvider;
    }

    @Override
    public OidcTokenResponse requestToken(String code) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", googleProperties.clientId());
            formData.add("client_secret", googleProperties.clientSecret());
            formData.add("redirect_uri", googleProperties.redirectUri());
            formData.add("code", code);

            return restClient.post()
                    .uri(googleProperties.tokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(GoogleTokenResponse.class);

        } catch (RestClientResponseException e) {
            log.error("구글 토큰 요청 실패 - status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BaseException(BaseErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
        } catch (Exception e) {
            log.error("구글 토큰 요청 실패: {}", e.getMessage(), e);
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
                    .withIssuer(googleProperties.issuer())
                    .withAudience(googleProperties.clientId())
                    .build()
                    .verify(idToken);

            // 페이로드 추출
            return new GoogleIdTokenPayload(
                    verifiedJwt.getIssuer(),
                    verifiedJwt.getAudience().get(0),
                    verifiedJwt.getSubject(),
                    verifiedJwt.getClaim("name").asString(),
                    verifiedJwt.getClaim("picture").asString(),
                    verifiedJwt.getClaim("email").asString()
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
