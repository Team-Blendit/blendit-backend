package kr.blendit.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.user.controller.dto.GetUserSearchRequest;
import kr.blendit.api.user.controller.dto.GetUserSearchResponse;
import kr.blendit.api.user.facade.UserSearchFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 검색 API")
@RestController
@RequestMapping("/api/blendit/user/search")
@RequiredArgsConstructor
public class UserSearchController {

  private final UserSearchFacade userSearchFacade;

  @Operation(summary = "유저 검색")
  @GetMapping
  public Page<GetUserSearchResponse> getUserSearch(
      CurrentUser currentUser,
      GetUserSearchRequest request,
      @PageableDefault Pageable pageable
  ) {
    return userSearchFacade.getUserSearch(currentUser, request, pageable);
  }
}
