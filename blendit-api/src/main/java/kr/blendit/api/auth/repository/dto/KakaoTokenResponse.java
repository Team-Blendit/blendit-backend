package kr.blendit.api.auth.repository.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.blendit.api.auth.dto.oidc.OidcTokenResponse;

public record KakaoTokenResponse(
        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("id_token")
        String idToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_token_expires_in")
        Integer refreshTokenExpiresIn,

        @JsonProperty("scope")
        String scope
) implements OidcTokenResponse {}
