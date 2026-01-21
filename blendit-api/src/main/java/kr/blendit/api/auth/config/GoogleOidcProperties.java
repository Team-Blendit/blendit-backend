package kr.blendit.api.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oidc.google")
public record GoogleOidcProperties(
    String clientId,
    String clientSecret,
    String redirectUri,
    String tokenUri,
    String jwksUri,
    String issuer
) {}
