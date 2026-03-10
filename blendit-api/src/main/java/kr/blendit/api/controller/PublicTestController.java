package kr.blendit.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.auth.dto.TokenDto;
import kr.blendit.api.auth.service.TokenService;
import kr.blendit.api.user.domain.User;
import kr.blendit.common.constant.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 인증 없이 접근 가능한 공개 API (시큐리티 미적용)
 * 경로: /api/blendit/auth/** 또는 /api/blendit/public/**
 */
@Tag(name = "Public API", description = "인증 없이 접근 가능한 공개 API")
@RestController
@RequestMapping("/api/blendit")
@RequiredArgsConstructor
public class PublicTestController {

    private final TokenService tokenService;

    @Operation(summary = "테스트용 토큰 발급", description = "개발용 JWT 토큰 발급 (RTR 방식, Redis 저장). 사용자가 없으면 자동 생성")
    @PostMapping("/auth/test-token")
    public Map<String, Object> issueTestToken(
            @RequestParam(defaultValue = "test@test.com") String email,
            @RequestParam(defaultValue = "USER") String role
    ) {
        UserRole userRole = UserRole.of("ROLE_" + role);

        // 테스트용: 사용자가 없으면 생성
        User user = tokenService.createUserIfNotExists(email, userRole);
        TokenDto token = tokenService.issueTokenPair(user.getUuid());

        return Map.of(
                "accessToken", token.accessToken(),
                "refreshToken", token.refreshToken(),
                "userUuid", user.getUuid(),
                "email", user.getEmail(),
                "role", user.getRole().getValue(),
                "message", "테스트용 토큰이 발급되었습니다. Authorization 헤더에 'Bearer {accessToken}' 형식으로 사용하세요."
        );
    }

    @Operation(summary = "토큰 재발급 (RTR)", description = "리프레시 토큰으로 새로운 토큰 쌍 발급. 기존 리프레시 토큰은 폐기됩니다.")
    @PostMapping("/auth/reissue")
    public Map<String, Object> reissue(@RequestParam String refreshToken) {
        TokenDto token = tokenService.reissue(refreshToken);

        return Map.of(
                "accessToken", token.accessToken(),
                "refreshToken", token.refreshToken(),
                "message", "토큰이 재발급되었습니다. 기존 리프레시 토큰은 더 이상 사용할 수 없습니다."
        );
    }

    @Operation(summary = "로그아웃 (현재 디바이스)", description = "현재 디바이스의 리프레시 토큰 폐기")
    @PostMapping("/auth/logout")
    public Map<String, String> logout(@RequestParam String refreshToken) {
        tokenService.logout(refreshToken);

        return Map.of(
                "message", "로그아웃 되었습니다."
        );
    }

    @Operation(summary = "토큰 없이 접근 테스트", description = "auth 경로는 인증 없이 접근 가능")
    @GetMapping("/auth/ping")
    public Map<String, String> ping() {
        return Map.of(
                "message", "pong - 인증 없이 접근 성공!",
                "path", "/api/blendit/auth/ping"
        );
    }
}
