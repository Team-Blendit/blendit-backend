package kr.blendit.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.blendit.common.constant.UserRole;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import kr.blendit.common.security.exception.JwtTokenErrorCode;
import kr.blendit.common.security.exception.JwtTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 토큰 검증 전용 Provider
 * 토큰 발급은 api 모듈의 TokenIssuer에서 담당
 */
@Component
@Slf4j
public class JwtProvider {

    private static final String AUTHORITIES_KEY = "roles";
    private static final int MIN_SECRET_KEY_BYTES = 32;

    private final SecretKey key;

    public JwtProvider(@Value("${jwt.token.secret-key}") String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_KEY_BYTES) {
            throw new BaseException(BaseErrorCode.INVALID_JWT_SECRET_KEY);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public UserToken parseToken(String token) throws JwtTokenException {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String userUuid = claimsJws.getBody().getSubject();
            Object rolesObj = claimsJws.getBody().get(AUTHORITIES_KEY);

            List<UserRole> roles = parseRoles(rolesObj);
            return new UserToken(userUuid, roles);
        } catch (SignatureException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
            log.debug("유효하지 않은 JWT 토큰: {}", ex.getMessage());
            throw new JwtTokenException(JwtTokenErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException expiredEx) {
            log.debug("만료된 JWT 토큰: {}", expiredEx.getMessage());
            throw new JwtTokenException(JwtTokenErrorCode.EXPIRED_TOKEN);
        }
    }

    @SuppressWarnings("unchecked")
    private List<UserRole> parseRoles(Object rolesObj) {
        if (rolesObj == null) {
            return List.of();
        }
        if (rolesObj instanceof List<?> rolesList) {
            return ((List<String>) rolesList).stream()
                    .map(UserRole::of)
                    .toList();
        }
        return List.of();
    }
}
