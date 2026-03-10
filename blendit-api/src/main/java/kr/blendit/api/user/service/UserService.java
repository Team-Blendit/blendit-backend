package kr.blendit.api.user.service;

import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.user.constant.LoginType;
import kr.blendit.api.user.domain.User;
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

  /**
   * 소셜 로그인 사용자 조회 또는 생성 - socialCompanyUserId로 기존 사용자 조회 - 없으면 신규 생성
   */
  @Transactional
  public User findOrCreateBySocialLogin(LoginType loginType, OidcUserInfo userInfo) {
    return userRepository.findByLoginTypeAndSocialCompanyUserId(loginType, userInfo.socialCompanyUserId())
        .orElseGet(() -> userRepository.save(User.createSocialUser(loginType, userInfo)));
  }

  public User getUser(String userUuid) {
    return userRepository.findByUuid(userUuid)
        .orElseThrow(() -> new BaseException(BaseErrorCode.USER_NOT_FOUND));
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
