package kr.blendit.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.blendit.api.user.controller.dto.UserOnboardingRequest;
import kr.blendit.api.user.controller.facade.UserFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "유저 API")
@RestController
@RequestMapping("/api/blendit/user")
@RequiredArgsConstructor
public class UserController {

  private final UserFacade userFacade;

  @Operation(summary = "온보딩")
  @PostMapping("/onboarding")
  public void onboarding(CurrentUser currentUser, @Valid @RequestBody UserOnboardingRequest request) {
    userFacade.onboarding(currentUser, request);
  }

}
