package kr.blendit.api.auth.service;

import kr.blendit.api.auth.dto.RefreshTokenInfo;
import kr.blendit.api.auth.dto.TokenDto;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenIssuer tokenIssuer;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;

    @Value("${jwt.token.refresh-exp-minute-time}")
    private Long refreshExpMinuteTime;

    /**
     * 토큰 쌍 발급 (로그인 시)
     */
    @Transactional(readOnly = true)
    public TokenDto issueTokenPair(String userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        String accessToken = tokenIssuer.createAccessToken(user.getUuid(), user.getRole());
        String refreshToken = createAndSaveRefreshToken(user.getUuid(), user.getTokenVersion());

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * RTR 방식 토큰 재발급
     */
    @Transactional(readOnly = true)
    public TokenDto reissue(String refreshToken) {
        RefreshTokenInfo tokenInfo = refreshTokenStore.findByToken(refreshToken)
                .orElseThrow(() -> new BaseException(BaseErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 기존 토큰 삭제 (RTR: 1회용)
        refreshTokenStore.delete(refreshToken);

        User user = userRepository.findByUuid(tokenInfo.userUuid())
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));

        // tokenVersion 검증: 전역 로그아웃 이후 발급된 토큰인지 확인
        if (tokenInfo.tokenVersion() != user.getTokenVersion()) {
            log.warn("무효화된 리프레시 토큰 사용 시도 - userUuid: {}", tokenInfo.userUuid());
            throw new BaseException(BaseErrorCode.TOKEN_REVOKED);
        }

        String newAccessToken = tokenIssuer.createAccessToken(user.getUuid(), user.getRole());
        String newRefreshToken = createAndSaveRefreshToken(user.getUuid(), user.getTokenVersion());

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    /**
     * 현재 디바이스 로그아웃
     */
    public void logout(String refreshToken) {
        refreshTokenStore.delete(refreshToken);
        log.info("리프레시 토큰으로 로그아웃 처리됨");
    }

    /**
     * 전체 디바이스 로그아웃 (tokenVersion 증가)
     */
    @Transactional
    public void logoutAll(String userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));
        user.incrementTokenVersion();
        log.info("사용자가 모든 디바이스에서 로그아웃됨: {}", userUuid);
    }

    /**
     * 테스트용 사용자 생성
     */
    @Transactional
    public User createUserIfNotExists(String email, kr.blendit.common.constant.UserRole role) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.createLocalUser(email, role)));
    }

    private String createAndSaveRefreshToken(String userUuid, int tokenVersion) {
        String token = UUID.randomUUID().toString();
        refreshTokenStore.save(token, userUuid, tokenVersion, Duration.ofMinutes(refreshExpMinuteTime));
        return token;
    }
}
