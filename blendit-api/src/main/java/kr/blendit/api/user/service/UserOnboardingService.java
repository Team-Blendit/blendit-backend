package kr.blendit.api.user.service;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.user.controller.dto.UserOnboardingRequest;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserKeywordRepository;
import kr.blendit.api.user.repository.UserRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserOnboardingService {

  private final UserRepository userRepository;
  private final UserKeywordRepository userKeywordRepository;
  private final UserService userservice;

  @Transactional
  public void onboarding(String userUuid, UserOnboardingRequest request, List<Keyword> keywordList) {
    User user = userservice.getUser(userUuid);
    if (!request.email().equals(user.getEmail())) {
      emailDuplicateCheck(request.email());
    }

    if (!request.nickname().equals(user.getNickname())) {
      nicknameDuplicateCheck(request.nickname());
    }

    user.onboarding(request, keywordList);
    userKeywordRepository.saveAll(user.getUserKeywords());
  }

  public void nicknameDuplicateCheck(String nickname) {
    if (userRepository.existsByNickname(nickname)) {
      throw new BaseException(BaseErrorCode.DUPLICATE_NICKNAME);
    }
  }

  public void emailDuplicateCheck(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new BaseException(BaseErrorCode.DUPLICATE_EMAIL);
    }
  }
}
