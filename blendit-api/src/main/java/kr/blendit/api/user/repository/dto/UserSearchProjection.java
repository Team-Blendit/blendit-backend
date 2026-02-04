package kr.blendit.api.user.repository.dto;

import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;

public record UserSearchProjection(
    String userUuid,
    String nickname,
    Position position,
    Experience experience,
    String province,
    String district,
    String profileImageUrl,
    boolean isBookmarked
) {

}
