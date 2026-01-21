package kr.blendit.api.user.domain;

import jakarta.persistence.*;
import kr.blendit.api.auth.dto.oidc.OidcUserInfo;
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
    @Column(nullable = false)
    private UserRole role;

    @Builder.Default
    @Column(nullable = false)
    private Integer tokenVersion = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    private String socialCompanyUserId;

    private String nickname;

    private String profileImage;

    /**
     * 소셜 로그인 사용자 생성
     */
    public static User createSocialUser(LoginType loginType, OidcUserInfo userInfo) {
        return User.builder()
                .loginType(loginType)
                .socialCompanyUserId(userInfo.socialCompanyUserId())
                .nickname(userInfo.nickname())
                .profileImage(userInfo.profileImage())
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

    public void updateProfile(String nickname, String profileImage) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }
}
