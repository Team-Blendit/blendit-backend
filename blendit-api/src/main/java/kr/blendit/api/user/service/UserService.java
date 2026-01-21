package kr.blendit.api.user.service;

import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.user.domain.LoginType;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.repository.UserRepository;
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
     * 소셜 로그인 사용자 조회 또는 생성
     * - socialCompanyUserId로 기존 사용자 조회
     * - 없으면 신규 생성
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
}
