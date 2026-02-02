package kr.blendit.api.blending.facade;

import java.util.List;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.response.BlendingListResponse;
import kr.blendit.api.blending.dto.response.MyAppliedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyCreatedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyHistoryBlendingResponse;
import kr.blendit.api.blending.service.BlendingMyQueryService;
import kr.blendit.api.blending.service.BlendingParticipationService;
import kr.blendit.api.blending.service.BlendingQueryService;
import kr.blendit.api.blending.service.BlendingService;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.keyword.service.KeywordService;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.service.UserService;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlendingFacade {

  private final BlendingService blendingService;
  private final BlendingParticipationService blendingParticipationService;
  private final BlendingQueryService blendingQueryService;
  private final UserService userService;
  private final KeywordService keywordService;
  private final BlendingMyQueryService blendingMyQueryService;

  /**
   * 블렌딩 생성 흐름 1. 블렌딩 및 키워드 생성 2. 호스트 권한 부여
   */
  @Transactional
  public void createBlending(String userUuid, BlendingCreateRequest request) {
    User user = userService.getUser(userUuid);
    List<Keyword> keywordList = keywordService.getKeywordList(request.getKeywordUuidList());
    blendingService.create(user, request, keywordList);
  }

  /**
   * 블렌딩 삭제 흐름 1. 호스트 검증 2. 논리 삭제 처리
   */
  @Transactional
  public void deleteBlending(String userUuid, String blendingUuid) {
    blendingService.delete(userUuid, blendingUuid);
  }

  /**
   * 블렌딩 정보 수정 흐름 1. 호스트 검증 2. 정보 및 키워드 업데이트
   */
  @Transactional
  public void updateBlending(String userUuid, String blendingUuid, BlendingUpdateRequest request) {
    blendingService.update(userUuid, blendingUuid, request);
  }

  /**
   * 블렌딩 상태 변경 흐름 1. 호스트 검증 2. 상태 업데이트
   */
  @Transactional
  public void updateBlendingStatus(String userUuid, String blendingUuid, BlendingStatus blendingStatus) {
    blendingService.updateStatus(userUuid, blendingUuid, blendingStatus);
  }

  /**
   * 블렌딩 상세 조회
   */
  @Transactional(readOnly = true)
  public BlendingDetailResponse getBlendingDetail(CurrentUser currentUser, String blendingUuid) {
    return blendingService.getBlendingDetail(currentUser, blendingUuid);
  }

  /**
   * 블렌딩 참여 신청
   */
  @Transactional
  public void applyParticipation(String userUuid, String blendingUuid, BlendingApplyRequest blendingApplyRequest) {
    User user = userService.getUser(userUuid);
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingParticipationService.apply(user, blending, blendingApplyRequest);
  }

  /**
   * 블렌딩 참여 신청 취소
   */
  @Transactional
  public void cancelParticipation(String userUuid, String blendingUuid) {
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingParticipationService.cancel(userUuid, blending);
  }

  /**
   * 블렌딩 참여 신청 내역 삭제
   */
  @Transactional
  public void deleteParticipation(String userUuid, String blendingUuid) {
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingParticipationService.delete(userUuid, blending);
  }

  /**
   * 블렌딩 참여 승인
   */
  @Transactional
  public void approveParticipation(String userUuid, String blendingUuid, String participantUuid) {
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingParticipationService.approve(userUuid, blending, participantUuid);
  }

  /**
   * 블렌딩 참여 거부
   */
  @Transactional
  public void rejectParticipation(String userUuid, String blendingUuid, String participantUuid) {
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingParticipationService.reject(userUuid, blending, participantUuid);
  }

  /**
   * 블렌딩 목록 조회
   */
  @Transactional(readOnly = true)
  public Page<BlendingListResponse> getBlendingList(
      CurrentUser currentUser, BlendingListRequest blendingListRequest, Pageable pageable) {
    return blendingQueryService.getList(currentUser, blendingListRequest, pageable);
  }

  /**
   * 블렌딩 북마크 추가
   */
  @Transactional
  public void addBookmark(String userUuid, String blendingUuid) {
    User user = userService.getUser(userUuid);
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingService.addBookmark(user, blending);
  }

  /**
   * 블렌딩 북마크 제거
   */
  @Transactional
  public void removeBookmark(String userUuid, String blendingUuid) {
    User user = userService.getUser(userUuid);
    Blending blending = blendingService.getBlending(blendingUuid);
    blendingService.removeBookmark(user, blending);
  }

  @Transactional(readOnly = true)
  public Page<MyCreatedBlendingResponse> getMyCreatedList(CurrentUser currentUser, Pageable pageable) {
    return blendingMyQueryService.getMyCreatedList(currentUser, pageable);
  }

  @Transactional(readOnly = true)
  public Page<MyAppliedBlendingResponse> getMyAppliedList(CurrentUser currentUser, Pageable pageable) {
    return blendingMyQueryService.getMyAppliedList(currentUser, pageable);
  }

  @Transactional(readOnly = true)
  public Page<MyHistoryBlendingResponse> getMyHistoryList(CurrentUser currentUser, Pageable pageable) {
    return blendingMyQueryService.getMyHistoryList(currentUser, pageable);
  }

}
