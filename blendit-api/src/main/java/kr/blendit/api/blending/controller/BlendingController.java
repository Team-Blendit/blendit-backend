package kr.blendit.api.blending.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.dto.request.BlendingCreateRequest;
import kr.blendit.api.blending.dto.response.BlendingDetailResponse;
import kr.blendit.api.blending.dto.request.BlendingUpdateRequest;
import kr.blendit.api.blending.facade.BlendingFacade;
import kr.blendit.api.blending.service.BlendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Blending API")
@RestController
@RequestMapping("/api/blendit/blending")
@RequiredArgsConstructor
public class BlendingController {

    private final BlendingService blendingService;
    private final BlendingFacade blendingFacade;

    @Operation(summary = "블렌딩 생성 API", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public void create(@AuthenticationPrincipal String userUuid, @RequestBody BlendingCreateRequest blendingCreateRequest) {

        blendingService.create(userUuid, blendingCreateRequest);
    }

    @Operation(summary = "블렌딩 논리 삭제 API", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{blendingUuid}")
    public void delete(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid) {

        blendingService.delete(userUuid, blendingUuid);
    }

    @Operation(summary = "블렌딩 정보 수정 API", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{blendingUuid}")
    public void update(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid, @RequestBody BlendingUpdateRequest blendingUpdateRequest) {

        blendingService.update(userUuid, blendingUuid, blendingUpdateRequest);
    }

    @Operation(summary = "블렌딩 상태 변경 API", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{blendingUuid}/status")
    public void updateStatus(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid, @RequestParam BlendingStatus blendingStatus) {

        blendingService.updateStatus(userUuid, blendingUuid, blendingStatus);
    }

    @Operation(summary = "블렌딩 상세 조회 API", description = "대기, 승인, 거절, 참여 중 상태의 모든 유저를 joinStatus 값으로 구분합니다.")
    @GetMapping("/{blendingUuid}")
    public BlendingDetailResponse find(@PathVariable String blendingUuid) {

        return blendingService.find(blendingUuid);
    }
}
