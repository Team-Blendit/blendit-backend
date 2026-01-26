package kr.blendit.api.user.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
import kr.blendit.api.user.constant.LoginType;
import kr.blendit.api.user.domain.converter.UserLinkListConverter;
import kr.blendit.api.user.domain.converter.StringListConverter;
import kr.blendit.api.common.constant.Position;
import kr.blendit.common.constant.UserRole;
import kr.blendit.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    private String email;

    @Enumerated(EnumType.STRING)
    private Position position;

    private String experienceYears;

    private String nickname;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private String province;

    private String district;

    private String affiliation;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> skills = new ArrayList<>();

    @Convert(converter = UserLinkListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<UserLink> links = new ArrayList<>();

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
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

    public void updateProfile(String nickname, String profileImage, String email) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
        if (email != null && this.email == null) {
            this.email = email;
        }
    }
}
