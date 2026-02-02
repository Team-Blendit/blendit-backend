package kr.blendit.api.user.facade;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.keyword.service.KeywordService;
import kr.blendit.api.user.controller.dto.UserOnboardingRequest;
import kr.blendit.api.user.service.UserOnboardingService;
import kr.blendit.api.user.service.UserService;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserOnboardingFacade {

  private final UserOnboardingService userOnboardingService;
  private final KeywordService keywordService;
  private final UserService userService;

  @Transactional
  public void onboarding(CurrentUser currentUser, UserOnboardingRequest request) {
    List<Keyword> keywordList = keywordService.getKeywordList(request.keywordUuidList());
    userOnboardingService.onboarding(currentUser.getUserUuid(), request, keywordList);
  }

  @Transactional(readOnly = true)
  public void emailDuplicateCheck(String email) {
    userService.emailDuplicateCheck(email);
  }

  @Transactional(readOnly = true)
  public void nicknameDuplicateCheck(String nickname) {
    userService.nicknameDuplicateCheck(nickname);
  }
}
