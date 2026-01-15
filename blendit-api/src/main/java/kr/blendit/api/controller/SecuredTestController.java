package kr.blendit.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.auth.service.TokenService;
import kr.blendit.common.constant.UserRole;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 인증이 필요한 보호된 API (시큐리티 적용)
 * 경로: /api/blendit-api/** (public, auth 제외)
 */
@Tag(name = "Secured API", description = "인증이 필요한 보호된 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/blendit")
@RequiredArgsConstructor
public class SecuredTestController {

    private final TokenService tokenService;

    @Operation(summary = "인증 필요 API", description = "JWT 토큰이 있어야 접근 가능")
    @GetMapping("/secured/test")
    public Map<String, Object> securedTest(CurrentUser currentUser) {
        return Map.of(
                "message", "인증된 사용자만 접근 가능한 API입니다.",
                "userUuid", currentUser.getUserUuid(),
                "roles", currentUser.getRoles().stream().map(UserRole::getValue).toList(),
                "isAdmin", currentUser.isAdmin(),
                "timestamp", LocalDateTime.now()
        );
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자 정보")
    @GetMapping("/users/me")
    public Map<String, Object> getMyInfo(CurrentUser currentUser) {
        return Map.of(
                "userUuid", currentUser.getUserUuid(),
                "roles", currentUser.getRoles().stream().map(UserRole::getValue).toList(),
                "isAdmin", currentUser.isAdmin(),
                "hasUserRole", currentUser.hasRole(UserRole.USER),
                "hasAdminRole", currentUser.hasRole(UserRole.ADMIN)
        );
    }

    @Operation(summary = "관리자 전용 API", description = "ADMIN 역할이 있어야 접근 가능 (비즈니스 로직으로 체크)")
    @GetMapping("/admin/test")
    public Map<String, Object> adminOnly(CurrentUser currentUser) {
        if (!currentUser.isAdmin()) {
            throw new BaseException(BaseErrorCode.ADMIN_ONLY);
        }

        return Map.of(
                "message", "관리자 전용 API에 접근했습니다.",
                "userUuid", currentUser.getUserUuid(),
                "adminAccess", true
        );
    }

    @Operation(summary = "토큰 검증 테스트", description = "토큰이 유효한지 확인")
    @GetMapping("/secured/validate")
    public Map<String, Object> validateToken(CurrentUser currentUser) {
        return Map.of(
                "valid", true,
                "userUuid", currentUser.getUserUuid(),
                "roles", currentUser.getRoles().stream().map(UserRole::getValue).toList(),
                "message", "토큰이 유효합니다."
        );
    }

    @Operation(summary = "전체 로그아웃", description = "모든 디바이스에서 로그아웃 (tokenVersion 증가)")
    @PostMapping("/auth/logout-all")
    public Map<String, String> logoutAll(CurrentUser currentUser) {
        tokenService.logoutAll(currentUser.getUserUuid());

        return Map.of(
                "message", "모든 디바이스에서 로그아웃 되었습니다.",
                "userUuid", currentUser.getUserUuid()
        );
    }
}
