package kr.blendit.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.blendit.api.user.controller.dto.GetMyProfileResponse;
import kr.blendit.api.user.controller.dto.GetUserProfileResponse;
import kr.blendit.api.user.controller.dto.UpdateUserProfileRequest;
import kr.blendit.api.user.facade.UserProfileFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "유저 프로필 API")
@RestController
@RequestMapping("/api/blendit/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private final UserProfileFacade userProfileFacade;

  @Operation(summary = "유저 프로필 수정")
  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void updateUserProfile(CurrentUser currentUser,
      @ModelAttribute @ParameterObject @Valid UpdateUserProfileRequest request,
      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
    userProfileFacade.updateUserProfile(currentUser, request, profileImage);
  }

  @Operation(summary = "유저 프로필 조회")
  @GetMapping("/{userUuid}")
  public GetUserProfileResponse getUserProfile(CurrentUser currentUser, @PathVariable String userUuid) {
    return userProfileFacade.getUserProfile(currentUser, userUuid);
  }

  @Operation(summary = "내 프로필 조회")
  @GetMapping("/me")
  public GetMyProfileResponse getMyProfile(CurrentUser currentUser) {
    return userProfileFacade.getMyProfile(currentUser);
  }
}
