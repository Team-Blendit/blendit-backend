package kr.blendit.api.user.controller.dto;

import java.util.List;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;

public record GetUserSearchResponse(
    String userUuid,
    String nickName,
    Position position,
    Experience experience,
    String province,
    String district,
    String profileImageUrl,
    List<String> keywordList,
    boolean isBookmarked
) {

}
