package kr.blendit.api.auth.repository.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.blendit.api.auth.dto.oidc.OidcTokenResponse;

public record GoogleTokenResponse(
        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("id_token")
        String idToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("scope")
        String scope
) implements OidcTokenResponse {}
