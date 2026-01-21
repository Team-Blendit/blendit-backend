package kr.blendit.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.blendit.api.auth.dto.OidcLoginRequest;
import kr.blendit.api.auth.dto.OidcLoginResponse;
import kr.blendit.api.auth.facade.OidcFacade;
import kr.blendit.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OIDC", description = "소셜 로그인 API (OIDC)")
@RestController
@RequestMapping("/api/blendit/auth/oidc")
@RequiredArgsConstructor
public class OidcController {

    private final OidcFacade oidcFacade;

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드로 로그인합니다. (OIDC 방식)")
    @PostMapping("/kakao")
    public OidcLoginResponse kakaoLogin(@Valid @RequestBody OidcLoginRequest request) {
      return oidcFacade.kakaoLogin(request.code());
    }
}
