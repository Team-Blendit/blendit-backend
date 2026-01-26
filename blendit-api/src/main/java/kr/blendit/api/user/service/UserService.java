package kr.blendit.api.user.service;

import java.util.List;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.user.constant.LoginType;
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
public class UserService {

  private final UserRepository userRepository;
  private final UserKeywordRepository userKeywordRepository;

  /**
   * 소셜 로그인 사용자 조회 또는 생성 - socialCompanyUserId로 기존 사용자 조회 - 없으면 신규 생성
   */
  @Transactional
  public User findOrCreateBySocialLogin(LoginType loginType, OidcUserInfo userInfo) {
    return userRepository.findByLoginTypeAndSocialCompanyUserId(loginType, userInfo.socialCompanyUserId())
        .map(existingUser -> {
          existingUser.updateProfile(userInfo.nickname(), userInfo.profileImage(), userInfo.email());
          return existingUser;
        })
        .orElseGet(() -> userRepository.save(User.createSocialUser(loginType, userInfo)));
  }

  @Transactional
  public void onboarding(String userUuid, UserOnboardingRequest request, List<Keyword> keywordList) {
    User user = getUser(userUuid);
    if (!user.getEmail().equals(request.email())) {
      emailDuplicateCheck(request.email());
    }

    if (!user.getNickname().equals(request.nickname())) {
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

  public User getUser(String userUuid) {
    return userRepository.findByUuid(userUuid)
        .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));
  }
}
