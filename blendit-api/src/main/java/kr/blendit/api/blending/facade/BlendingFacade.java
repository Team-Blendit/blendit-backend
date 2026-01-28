package kr.blendit.api.blending.facade;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.response.BlendingListResponse;
import kr.blendit.api.blending.service.BlendingParticipationService;
import kr.blendit.api.blending.service.BlendingQueryService;
import kr.blendit.api.blending.service.BlendingService;
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

    /**
     * 블렌딩 생성 흐름
     * 1. 블렌딩 및 키워드 생성
     * 2. 호스트 권한 부여
     */
    @Transactional
    public void createBlending(String userUuid, BlendingCreateRequest request) {
        blendingService.create(userUuid, request);
    }

    /**
     * 블렌딩 삭제 흐름
     * 1. 호스트 검증
     * 2. 논리 삭제 처리
     */
    @Transactional
    public void deleteBlending(String userUuid, String blendingUuid) {
        blendingService.delete(userUuid, blendingUuid);
    }

    /**
     * 블렌딩 정보 수정 흐름
     * 1. 호스트 검증
     * 2. 정보 및 키워드 업데이트
     */
    @Transactional
    public void updateBlending(String userUuid, String blendingUuid, BlendingUpdateRequest request) {
        blendingService.update(userUuid, blendingUuid, request);
    }

    /**
     * 블렌딩 상태 변경 흐름
     * 1. 호스트 검증
     * 2. 상태 업데이트
     */
    @Transactional
    public void updateBlendingStatus(String userUuid, String blendingUuid, BlendingStatus blendingStatus) {
        blendingService.updateStatus(userUuid, blendingUuid, blendingStatus);
    }

    /**
     * 블렌딩 상세 조회
     */
    @Transactional(readOnly = true)
    public BlendingDetailResponse getBlendingDetail(String userUuid, String blendingUuid) {
        return blendingService.find(userUuid, blendingUuid);
    }

    /**
     * 블렌딩 참여 신청
     */
    @Transactional
    public void applyParticipation(String userUuid, String blendingUuid, BlendingApplyRequest blendingApplyRequest) {
        blendingParticipationService.apply(userUuid, blendingUuid, blendingApplyRequest);
    }

    /**
     * 블렌딩 참여 신청 취소
     */
    @Transactional
    public void cancelParticipation(String userUuid, String blendingUuid) {
        blendingParticipationService.cancel(userUuid,blendingUuid);
    }

    /**
     * 블렌딩 참여 승인
     */
    @Transactional
    public void approveParticipation(String userUuid, String blendingUuid, String participantUuid) {
        blendingParticipationService.approve(userUuid, blendingUuid, participantUuid);
    }

    /**
     * 블렌딩 참여 거부
     */
    @Transactional
    public void rejectParticipation(String userUuid, String blendingUuid, String participantUuid) {
        blendingParticipationService.reject(userUuid, blendingUuid, participantUuid);
    }

    /**
     * 블렌딩 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<BlendingListResponse> getBlendingList(
            String userUuid, BlendingListRequest blendingListRequest, Pageable pageable) {
        return blendingQueryService.getList(userUuid, blendingListRequest, pageable);
    }
}
