package kr.blendit.api.auth.service;

import kr.blendit.api.auth.dto.RefreshTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String TOKEN_PREFIX = "rt:";

    private final StringRedisTemplate redisTemplate;

    /**
     * 리프레시 토큰 저장
     * rt:{token} → userUuid:tokenVersion
     */
    public void save(String token, String userUuid, int tokenVersion, Duration ttl) {
        String value = userUuid + ":" + tokenVersion;
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, value, ttl);
    }

    /**
     * 토큰으로 사용자 정보 조회
     */
    public Optional<RefreshTokenInfo> findByToken(String token) {
        String value = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (value == null) {
            return Optional.empty();
        }

        String[] parts = value.split(":", 2);
        if (parts.length < 2) {
            return Optional.empty();
        }

        return Optional.of(new RefreshTokenInfo(parts[0], Integer.parseInt(parts[1])));
    }

    /**
     * 토큰 삭제
     */
    public void delete(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
