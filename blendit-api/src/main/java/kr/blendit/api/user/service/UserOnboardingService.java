package kr.blendit.api.user.service;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.user.controller.dto.UserOnboardingRequest;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserOnboardingService {

  private final UserKeywordRepository userKeywordRepository;
  private final UserService userService;

  @Transactional
  public void onboarding(String userUuid, UserOnboardingRequest request, List<Keyword> keywordList) {
    User user = userService.getUser(userUuid);
    if (!request.email().equals(user.getEmail())) {
      userService.emailDuplicateCheck(request.email());
    }

    if (!request.nickname().equals(user.getNickname())) {
      userService.nicknameDuplicateCheck(request.nickname());
    }

    user.onboarding(request, keywordList);
    userKeywordRepository.saveAll(user.getUserKeywords());
  }
}
