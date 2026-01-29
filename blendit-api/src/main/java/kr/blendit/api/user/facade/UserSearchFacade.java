package kr.blendit.api.user.facade;

import kr.blendit.api.user.controller.dto.GetUserSearchRequest;
import kr.blendit.api.user.controller.dto.GetUserSearchResponse;
import kr.blendit.api.user.service.UserSearchService;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSearchFacade {

  private final UserSearchService userSearchService;

  public Page<GetUserSearchResponse> getUserSearch(CurrentUser currentUser, GetUserSearchRequest request, Pageable pageable) {
    String currentUserUuid = currentUser != null ? currentUser.getUserUuid() : null;
    return userSearchService.searchUsers(request, currentUserUuid, pageable);
  }
}
