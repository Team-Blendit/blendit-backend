package kr.blendit.api.user.facade;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.keyword.service.KeywordService;
import kr.blendit.api.user.controller.dto.GetMyProfileResponse;
import kr.blendit.api.user.controller.dto.GetUserProfileResponse;
import kr.blendit.api.user.controller.dto.UpdateUserProfileRequest;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.service.UserProfileService;
import kr.blendit.api.user.service.UserService;
import kr.blendit.common.security.jwt.CurrentUser;
import kr.blendit.common.storage.dto.UploadedObject;
import kr.blendit.common.storage.service.LocalObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileFacade {

  private final UserProfileService userProfileService;
  private final KeywordService keywordService;
  private final LocalObjectStorageService localObjectStorageService;
  private final UserService userService;

  @Transactional
  public void updateUserProfile(CurrentUser currentUser, UpdateUserProfileRequest request, MultipartFile profileImage) {
    User user = userService.getUser(currentUser.getUserUuid());
    localObjectStorageService.deleteByUrl(user.getProfileImage());
    UploadedObject uploadedObject = localObjectStorageService.upload(profileImage, "user-profile-image");
    List<Keyword> keywordList = keywordService.getKeywordList(request.keywordUuidList());
    userProfileService.updateUserProfile(
        currentUser,
        request,
        keywordList,
        uploadedObject == null ? null : uploadedObject.url());
  }

  @Transactional(readOnly = true)
  public GetUserProfileResponse getUserProfile(CurrentUser currentUser, String userUuid) {
    String currentUserUuid = currentUser != null ? currentUser.getUserUuid() : null;
    return userProfileService.getUserProfile(currentUserUuid, userUuid);
  }

  @Transactional(readOnly = true)
  public GetMyProfileResponse getMyProfile(CurrentUser currentUser) {
    return userProfileService.getMyProfile(currentUser.getUserUuid());
  }
}
