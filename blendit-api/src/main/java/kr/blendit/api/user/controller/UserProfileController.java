package kr.blendit.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.blendit.api.user.controller.dto.UpdateUserProfileRequest;
import kr.blendit.api.user.facade.UserProfileFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 프로필 API")
@RestController
@RequestMapping("/api/blendit/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private final UserProfileFacade userProfileFacade;

  @Operation(summary = "유저 프로필 수정")
  @PutMapping
  public void updateUserProfile(CurrentUser currentUser, @RequestBody @Valid UpdateUserProfileRequest request) {
    userProfileFacade.updateUserProfile(currentUser, request);
  }
}
