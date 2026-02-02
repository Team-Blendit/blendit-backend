package kr.blendit.api.user.controller.dto;

import java.util.List;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.domain.UserBookmark;

public record UserBookmarkListResponse(
    String userUuid,
    String nickname,
    Position position,
    Experience experience,
    String province,
    String district,
    List<String> keywords,
    String profileImageUrl
) {

  public static UserBookmarkListResponse create(UserBookmark userBookmark) {
    User bookmarkUser = userBookmark.getBookmarkUser();
    return new UserBookmarkListResponse(
        bookmarkUser.getUuid(),
        bookmarkUser.getNickname(),
        bookmarkUser.getPosition(),
        bookmarkUser.getExperience(),
        bookmarkUser.getProvince(),
        bookmarkUser.getDistrict(),
        //TODO : N+1 성능 문제 생길 시 수정 필요
        bookmarkUser.getUserKeywords().stream()
            .map(k -> k.getKeyword().getName())
            .toList(),
        bookmarkUser.getProfileImage()
    );
  }
}
