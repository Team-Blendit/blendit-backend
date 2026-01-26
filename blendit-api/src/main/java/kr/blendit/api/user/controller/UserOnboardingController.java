package kr.blendit.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.blendit.api.user.controller.dto.UserOnboardingRequest;
import kr.blendit.api.user.controller.facade.UserFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 온보딩 API")
@RestController
@RequestMapping("/api/blendit/user/onboarding")
@RequiredArgsConstructor
public class UserOnboardingController {

  private final UserFacade userFacade;

  @Operation(summary = "온보딩")
  @PostMapping
  public void onboarding(CurrentUser currentUser, @Valid @RequestBody UserOnboardingRequest request) {
    userFacade.onboarding(currentUser, request);
  }

  @Operation(summary = "온보딩 이메일 중복 체크")
  @GetMapping("/email-duplicate-check")
  public void emailDuplicateCheck(String email) {
    userFacade.emailDuplicateCheck(email);
  }
}
