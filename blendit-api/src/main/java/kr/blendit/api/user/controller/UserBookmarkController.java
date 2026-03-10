package kr.blendit.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.user.controller.dto.UserBookmarkListResponse;
import kr.blendit.api.user.facade.UserBookmarkFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 북마크 API")
@RestController
@RequestMapping("/api/blendit/user/bookmark")
@RequiredArgsConstructor
public class UserBookmarkController {

  private final UserBookmarkFacade userBookmarkFacade;

  @Operation(summary = "유저 북마크 등록")
  @PostMapping("/{userUuid}")
  public void createUserBookMark(CurrentUser currentUser, @PathVariable String userUuid) {
    userBookmarkFacade.createUserBookMark(currentUser, userUuid);
  }

  @Operation(summary = "유저 북마크 삭제")
  @DeleteMapping("/{userUuid}")
  public void deleteUserBookMark(CurrentUser currentUser, @PathVariable String userUuid) {
    userBookmarkFacade.deleteUserBookMark(currentUser, userUuid);
  }

  @Operation(summary = "유저 북마크 리스트 조회")
  @GetMapping("/list")
  public Page<UserBookmarkListResponse> getUserBookMarkList(CurrentUser currentUser, @PageableDefault Pageable pageable) {
    return userBookmarkFacade.getUserBookMarkList(currentUser, pageable);
  }
}
