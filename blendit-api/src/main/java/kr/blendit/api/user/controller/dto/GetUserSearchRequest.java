package kr.blendit.api.user.controller.dto;

import java.util.List;
import kr.blendit.api.common.constant.Position;

public record GetUserSearchRequest(

    Position position,

    List<String> keywordUuidList,

    List<String> districtList,

    Boolean isBookmarked
) {

}
