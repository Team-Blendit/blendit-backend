package kr.blendit.api.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.blendit.common.constant.UserRole;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class TokenIssuer {

    private static final String AUTHORITIES_KEY = "roles";
    private static final int MIN_SECRET_KEY_BYTES = 32;

    private final SecretKey key;
    private final Long expirationMinuteTime;
    private final String issuer;

    public TokenIssuer(
            @Value("${jwt.token.secret-key}") String key,
            @Value("${jwt.token.exp-minute-time}") long expirationMinuteTime,
            @Value("${jwt.token.issuer}") String issuer
    ) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_KEY_BYTES) {
            throw new BaseException(BaseErrorCode.INVALID_JWT_SECRET_KEY);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMinuteTime = expirationMinuteTime;
        this.issuer = issuer;
    }

    public String createAccessToken(String userUuid, UserRole role) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = issuedAt.plusMinutes(expirationMinuteTime);

        Claims claims = Jwts.claims().setSubject(userUuid);
        claims.put(AUTHORITIES_KEY, List.of(role.getValue()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(toDate(issuedAt))
                .setExpiration(toDate(expiredAt))
                .signWith(key)
                .compact();
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
