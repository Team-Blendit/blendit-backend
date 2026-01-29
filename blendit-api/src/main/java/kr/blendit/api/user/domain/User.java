package kr.blendit.api.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.user.constant.Experience;
import kr.blendit.api.user.constant.LoginType;
import kr.blendit.api.user.controller.dto.UpdateUserProfileRequest;
import kr.blendit.api.user.controller.dto.UserOnboardingRequest;
import kr.blendit.api.user.domain.converter.UserLinkConverter;
import kr.blendit.api.user.domain.converter.UserSkillConverter;
import kr.blendit.common.constant.UserRole;
import kr.blendit.common.entity.BaseEntity;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

  @Comment("이메일")
  private String email;

  @Comment("직군")
  @Enumerated(EnumType.STRING)
  private Position position;

  @Comment("경력")
  @Enumerated(EnumType.STRING)
  private Experience experience;

  @Comment("닉네임")
  private String nickname;

  @Comment("소개")
  @Column(columnDefinition = "TEXT")
  private String description;

  @Comment("도/광역시")
  private String province;

  @Comment("시/군/구")
  private String district;

  @Comment("소속")
  private String affiliation;

  @Comment("기술 스택")
  @Convert(converter = UserSkillConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<UserSkill> skills = new ArrayList<>();

  @Comment("링크")
  @Convert(converter = UserLinkConverter.class)
  @Column(columnDefinition = "TEXT")
  private List<UserLink> links = new ArrayList<>();

  @Comment("프로필 이미지")
  private String profileImage;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginType loginType;

  private String socialCompanyUserId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Column(nullable = false)
  @Builder.Default
  private Integer tokenVersion = 0;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
  @Builder.Default
  private List<UserKeyword> userKeywords = new ArrayList<>();

  /**
   * 소셜 로그인 사용자 생성
   */
  public static User createSocialUser(LoginType loginType, OidcUserInfo userInfo) {
    return User.builder()
        .loginType(loginType)
        .socialCompanyUserId(userInfo.socialCompanyUserId())
        .nickname(userInfo.nickname())
        .profileImage(userInfo.profileImage())
        .email(userInfo.email())
        .role(UserRole.USER)
        .build();
  }

  /**
   * 로컬 사용자 생성 (테스트용)
   */
  public static User createLocalUser(String email, UserRole role) {
    return User.builder()
        .email(email)
        .role(role)
        .loginType(LoginType.LOCAL)
        .build();
  }

  public void incrementTokenVersion() {
    this.tokenVersion++;
  }

  public void onboarding(UserOnboardingRequest request, List<Keyword> keywordList) {
    if (isOnboardingComplete()) {
      throw new BaseException(BaseErrorCode.ALREADY_ONBOARDED);
    }
    this.position = request.position();
    this.experience = request.experience();
    this.email = request.email();
    this.province = request.province();
    this.district = request.district();
    this.nickname = request.nickname();
    this.userKeywords = keywordList.stream()
        .map(keyword -> UserKeyword.create(this, keyword))
        .toList();
  }

  public boolean isOnboardingComplete() {
    return !this.userKeywords.isEmpty();
  }

  public void updateProfile(UpdateUserProfileRequest request, List<Keyword> keywordList) {
    this.nickname = request.nickname();
    this.description = request.description();
    this.experience = request.experience();
    this.position = request.position();
    this.province = request.province();
    this.district = request.district();
    this.affiliation = request.affiliation();
    this.skills = request.skills();
    this.links = request.links();
    updateUserKeywords(keywordList);
  }

  private void updateUserKeywords(List<Keyword> newKeywords) {
    Map<Keyword, UserKeyword> existingByKeyword = this.userKeywords.stream()
        .collect(Collectors.toMap(UserKeyword::getKeyword, Function.identity()));

    List<UserKeyword> updatedKeywords = newKeywords.stream()
        .map(keyword ->
            existingByKeyword.getOrDefault(keyword, UserKeyword.create(this, keyword)))
        .toList();

    this.userKeywords.clear();
    this.userKeywords.addAll(updatedKeywords);
  }
}
