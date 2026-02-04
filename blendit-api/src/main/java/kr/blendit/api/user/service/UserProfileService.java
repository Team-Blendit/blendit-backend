package kr.blendit.api.user.service;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.user.controller.dto.GetMyProfileResponse;
import kr.blendit.api.user.controller.dto.GetUserProfileResponse;
import kr.blendit.api.user.controller.dto.UpdateUserProfileRequest;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserBookmarkRepository;
import kr.blendit.api.user.repository.UserKeywordRepository;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

  private final UserService userService;
  private final UserKeywordRepository userKeywordRepository;
  private final UserBookmarkRepository userBookmarkRepository;

  @Transactional
  public void updateUserProfile(CurrentUser currentUser, UpdateUserProfileRequest request, List<Keyword> keywordList,
      String profileImageUrl) {
    User user = userService.getUser(currentUser.getUserUuid());

    if (!request.email().equals(user.getEmail())) {
      userService.emailDuplicateCheck(request.email());
    }

    if (!request.nickname().equals(user.getNickname())) {
      userService.nicknameDuplicateCheck(request.nickname());
    }

    user.updateProfile(request, keywordList, profileImageUrl);
    userKeywordRepository.saveAll(user.getUserKeywords());
  }

  public GetUserProfileResponse getUserProfile(String currentUserUuid, String userUuid) {
    User targetUser = userService.getUser(userUuid);

    boolean isBookmarked = false;
    if (currentUserUuid != null) {
      User currentUser = userService.getUser(currentUserUuid);
      isBookmarked = userBookmarkRepository.existsByUserAndBookmarkUser(currentUser, targetUser);
    }

    return GetUserProfileResponse.create(targetUser, isBookmarked);
  }

  public GetMyProfileResponse getMyProfile(String userUuid) {
    User user = userService.getUser(userUuid);
    return GetMyProfileResponse.create(user);
  }
}
