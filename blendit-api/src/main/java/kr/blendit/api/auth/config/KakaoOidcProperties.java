package kr.blendit.api.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oidc.kakao")
public record KakaoOidcProperties(
    String clientId,
    String clientSecret,
    String redirectUri,
    String tokenUri,
    String jwksUri,
    String issuer
) {}
