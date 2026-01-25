package kr.blendit.api.blending.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.dto.request.BlendingApplyRequest;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.facade.BlendingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Blending API")
@RestController
@RequestMapping("/api/blendit/blending")
@RequiredArgsConstructor
public class BlendingController {

    private final BlendingFacade blendingFacade;

    @Operation(summary = "블렌딩 생성 API", security = @SecurityRequirement(name = "bearerAuth"), description = "openChattingUrl은 생략할 수 있습니다.")
    @PostMapping
    public void createBlending(@AuthenticationPrincipal String userUuid, @RequestBody BlendingCreateRequest blendingCreateRequest) {

        blendingFacade.createBlending(userUuid, blendingCreateRequest);
    }

    @Operation(summary = "블렌딩 논리 삭제 API", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{blendingUuid}")
    public void deleteBlending(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid) {

        blendingFacade.deleteBlending(userUuid, blendingUuid);
    }

    @Operation(summary = "블렌딩 정보 수정 API", security = @SecurityRequirement(name = "bearerAuth"), description = "openChattingUrl은 생략할 수 있습니다.")
    @PatchMapping("/{blendingUuid}")
    public void updateBlending(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid, @RequestBody BlendingUpdateRequest blendingUpdateRequest) {

        blendingFacade.updateBlending(userUuid, blendingUuid, blendingUpdateRequest);
    }

    @Operation(summary = "블렌딩 상태 변경 API", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{blendingUuid}/status")
    public void updateBlendingStatus(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid, @RequestParam BlendingStatus blendingStatus) {

        blendingFacade.updateBlendingStatus(userUuid, blendingUuid, blendingStatus);
    }

    @Operation(summary = "블렌딩 상세 조회 API", security = @SecurityRequirement(name = "bearerAuth"), description = "대기, 승인, 거절, 참여 중 상태의 모든 유저를 joinStatus 값으로 구분합니다.")
    @GetMapping("/{blendingUuid}")
    public BlendingDetailResponse getBlendingDetail(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid) {

        return blendingFacade.getBlendingDetail(userUuid, blendingUuid);
    }

    @Operation(summary = "블렌딩 참여 신청 API", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{blendingUuid}/participation")
    public void applyParticipation(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid, @RequestBody BlendingApplyRequest blendingApplyRequest) {

        blendingFacade.applyParticipation(userUuid, blendingUuid, blendingApplyRequest);
    }

    @Operation(summary = "블렌딩 참여 신청 API", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{blendingUuid}/participation")
    public void cancelParticipation(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid) {

        blendingFacade.cancelParticipation(userUuid, blendingUuid);
    }


}
