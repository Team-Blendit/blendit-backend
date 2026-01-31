package kr.blendit.api.blending.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.facade.BlendingFacade;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Blending API")
@RestController
@RequestMapping("/api/blendit/blending")
@RequiredArgsConstructor
public class BlendingController {

    private final BlendingFacade blendingFacade;

    @Operation(summary = "블렌딩 생성 API", description = "openChattingUrl은 생략할 수 있습니다.")
    @PostMapping
    public void createBlending(CurrentUser currentUser, @RequestBody @Valid BlendingCreateRequest blendingCreateRequest) {

        blendingFacade.createBlending(currentUser.getUserUuid(), blendingCreateRequest);
    }

    @Operation(summary = "블렌딩 논리 삭제 API")
    @DeleteMapping("/{blendingUuid}")
    public void deleteBlending(CurrentUser currentUser, @PathVariable String blendingUuid) {

        blendingFacade.deleteBlending(currentUser.getUserUuid(), blendingUuid);
    }

    @Operation(summary = "블렌딩 정보 수정 API", description = "openChattingUrl은 생략할 수 있습니다.")
    @PatchMapping("/{blendingUuid}")
    public void updateBlending(CurrentUser currentUser, @PathVariable String blendingUuid, @RequestBody BlendingUpdateRequest blendingUpdateRequest) {

        blendingFacade.updateBlending(currentUser.getUserUuid(), blendingUuid, blendingUpdateRequest);
    }

    @Operation(summary = "블렌딩 상태 변경 API")
    @PatchMapping("/{blendingUuid}/status")
    public void updateBlendingStatus(CurrentUser currentUser, @PathVariable String blendingUuid, @RequestParam BlendingStatus blendingStatus) {

        blendingFacade.updateBlendingStatus(currentUser.getUserUuid(), blendingUuid, blendingStatus);
    }

    @Operation(summary = "블렌딩 상세 조회 API", description = "대기, 승인, 거절, 참여 중 상태의 모든 유저를 joinStatus 값으로 구분합니다.")
    @GetMapping("/{blendingUuid}")
    public BlendingDetailResponse getBlendingDetail(CurrentUser currentUser, @PathVariable String blendingUuid) {

        return blendingFacade.getBlendingDetail(currentUser, blendingUuid);
    }

    @Operation(summary = "블렌딩 참여 신청 API")
    @PostMapping("/{blendingUuid}/participation")
    public void applyParticipation(CurrentUser currentUser, @PathVariable String blendingUuid, @RequestBody BlendingApplyRequest blendingApplyRequest) {

        blendingFacade.applyParticipation(currentUser.getUserUuid(), blendingUuid, blendingApplyRequest);
    }

    @Operation(summary = "블렌딩 참여 신청 취소 API")
    @DeleteMapping("/{blendingUuid}/participation")
    public void cancelParticipation(CurrentUser currentUser, @PathVariable String blendingUuid) {

        blendingFacade.cancelParticipation(currentUser.getUserUuid(), blendingUuid);
    }

    @Operation(summary = "블렌딩 참여 승인 API")
    @PatchMapping("/{blendingUuid}/participation/{participantUuid}/approve")
    public void approveParticipation(CurrentUser currentUser, @PathVariable String blendingUuid, @PathVariable String participantUuid) {

        blendingFacade.approveParticipation(currentUser.getUserUuid(), blendingUuid, participantUuid);
    }

    @Operation(summary = "블렌딩 참여 거부 API")
    @PatchMapping("/{blendingUuid}/participation/{participantUuid}/reject")
    public void rejectParticipation(CurrentUser currentUser, @PathVariable String blendingUuid, @PathVariable String participantUuid) {

        blendingFacade.rejectParticipation(currentUser.getUserUuid(), blendingUuid, participantUuid);
    }
}
