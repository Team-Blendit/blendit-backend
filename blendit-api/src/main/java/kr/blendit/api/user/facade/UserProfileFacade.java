package kr.blendit.api.user.facade;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.keyword.service.KeywordService;
import kr.blendit.api.user.controller.dto.UpdateUserProfileRequest;
import kr.blendit.api.user.service.UserProfileService;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileFacade {

  private final UserProfileService userProfileService;
  private final KeywordService keywordService;

  @Transactional
  public void updateUserProfile(CurrentUser currentUser, UpdateUserProfileRequest request) {
    List<Keyword> keywordList = keywordService.getKeywordList(request.keywordUuidList());
    userProfileService.updateUserProfile(currentUser, request, keywordList);
  }
}
