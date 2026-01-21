package kr.blendit.api.auth.dto;

import kr.blendit.api.user.domain.LoginType;
import kr.blendit.api.user.domain.User;

public record OidcLoginResponse(
        String accessToken,
        String refreshToken,
        UserInfo user
) {

    public static OidcLoginResponse of(TokenDto tokenDto, User user) {
        return new OidcLoginResponse(
                tokenDto.accessToken(),
                tokenDto.refreshToken(),
                UserInfo.from(user)
        );
    }

    public record UserInfo(
            String uuid,
            String nickname,
            String profileImage,
            LoginType loginType
    ) {
        public static UserInfo from(User user) {
            return new UserInfo(
                    user.getUuid(),
                    user.getNickname(),
                    user.getProfileImage(),
                    user.getLoginType()
            );
        }
    }
}
