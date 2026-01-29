package kr.blendit.api.user.service;

import kr.blendit.api.user.controller.dto.UserBookmarkListResponse;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.domain.UserBookmark;
import kr.blendit.api.user.repository.UserBookmarkRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBookmarkService {

  private final UserService userService;
  private final UserBookmarkRepository userBookmarkRepository;

  @Transactional
  public void createUserBookMark(CurrentUser currentUser, String userUuid) {
    User bookmarkUser = userService.getUser(userUuid);
    User user = userService.getUser(currentUser.getUserUuid());

    if (user.getUuid().equals(bookmarkUser.getUuid())) {
      throw new BaseException(BaseErrorCode.CANNOT_BOOKMARK_SELF);
    }

    if (userBookmarkRepository.existsByUserAndBookmarkUser(user, bookmarkUser)) {
      throw new BaseException(BaseErrorCode.DUPLICATE_USER_BOOKMARK);
    }

    UserBookmark userBookmark = UserBookmark.create(user, bookmarkUser);
    userBookmarkRepository.save(userBookmark);
  }

  @Transactional
  public void deleteUserBookMark(CurrentUser currentUser, String userUuid) {
    User bookmarkUser = userService.getUser(userUuid);
    User user = userService.getUser(currentUser.getUserUuid());

    UserBookmark userBookmark = userBookmarkRepository.findByUserAndBookmarkUser(user, bookmarkUser)
        .orElseThrow(() -> new BaseException(BaseErrorCode.USER_BOOKMARK_NOT_FOUND));

    userBookmarkRepository.delete(userBookmark);
  }

  public Page<UserBookmarkListResponse> getUserBookMarkList(CurrentUser currentUser, Pageable pageable) {
    User user = userService.getUser(currentUser.getUserUuid());
    return userBookmarkRepository.findAllByUserOrderByCreatedDateDesc(user, pageable)
        .map(UserBookmarkListResponse::create);
  }
}
