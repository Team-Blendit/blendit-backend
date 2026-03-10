package kr.blendit.api.auth.config;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Configuration
public class JwkProviderConfig {

    private static final int MAX_CACHED_KEYS = 10;
    private static final long CACHE_DURATION_HOURS = 24;
    private static final int RATE_LIMIT_PER_MINUTE = 10;

    @Bean
    public JwkProvider kakaoJwkProvider(KakaoOidcProperties properties) {
        return createJwkProvider(properties.jwksUri(), "카카오");
    }

    @Bean
    public JwkProvider googleJwkProvider(GoogleOidcProperties properties) {
        return createJwkProvider(properties.jwksUri(), "구글");
    }

    private JwkProvider createJwkProvider(String jwksUri, String providerName) {
        try {
            return new JwkProviderBuilder(new URL(jwksUri))
                    .cached(MAX_CACHED_KEYS, CACHE_DURATION_HOURS, TimeUnit.HOURS)
                    .rateLimited(RATE_LIMIT_PER_MINUTE, 1, TimeUnit.MINUTES)
                    .build();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(providerName + " JWKS Provider 초기화 실패: " + jwksUri, e);
        }
    }
}
