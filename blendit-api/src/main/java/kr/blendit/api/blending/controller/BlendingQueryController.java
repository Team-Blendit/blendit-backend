package kr.blendit.api.blending.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.blending.dto.response.BlendingListResponse;
import kr.blendit.api.blending.dto.response.MyAppliedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyCreatedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyHistoryBlendingResponse;
import kr.blendit.api.blending.facade.BlendingFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blending Query API")
@RestController
@RequestMapping("/api/blendit/blending/query")
@RequiredArgsConstructor
public class BlendingQueryController {

  private final BlendingFacade blendingFacade;

  @Operation(summary = "블렌딩 전체 목록 조회 API")
  @GetMapping
  public Page<BlendingListResponse> getBlendingList(
          CurrentUser currentUser,
          @ParameterObject @ModelAttribute BlendingListRequest blendingListRequest,
          @ParameterObject @PageableDefault(size = 20) Pageable pageable) {

    return blendingFacade.getBlendingList(currentUser, blendingListRequest, pageable);
  }

  @Operation(summary = "내가 신청한 블렌딩 목록 조회 API")
  @GetMapping("/my/applied")
  public Page<MyAppliedBlendingResponse> getMyAppliedList(
          CurrentUser currentUser,
          @ParameterObject @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

    return blendingFacade.getMyAppliedList(currentUser, pageable);
  }

  @Operation(summary = "내가 생성한 블렌딩 목록 조회 API")
  @GetMapping("/my/created")
  public Page<MyCreatedBlendingResponse> getMyCreatedList(
          CurrentUser currentUser,
          @ParameterObject @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
    return blendingFacade.getMyCreatedList(currentUser, pageable);
  }

  @Operation(summary = "나의 활동 내역 조회 API")
  @GetMapping("/my/history")
  public Page<MyHistoryBlendingResponse> getMyHistoryList(
          CurrentUser currentUser,
          @ParameterObject @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
    return blendingFacade.getMyHistoryList(currentUser, pageable);
  }
}



