package kr.blendit.api.user.facade;

import kr.blendit.api.user.controller.dto.UserBookmarkListResponse;
import kr.blendit.api.user.service.UserBookmarkService;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBookmarkFacade {

  private final UserBookmarkService userBookmarkService;

  @Transactional
  public void createUserBookMark(CurrentUser currentUser, String userUuid) {
    userBookmarkService.createUserBookMark(currentUser, userUuid);
  }

  @Transactional
  public void deleteUserBookMark(CurrentUser currentUser, String userUuid) {
    userBookmarkService.deleteUserBookMark(currentUser, userUuid);
  }

  @Transactional(readOnly = true)
  public Page<UserBookmarkListResponse> getUserBookMarkList(CurrentUser currentUser, Pageable pageable) {
    return userBookmarkService.getUserBookMarkList(currentUser, pageable);
  }
}
