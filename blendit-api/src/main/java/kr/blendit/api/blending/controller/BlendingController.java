package kr.blendit.api.blending.controller;

import kr.blendit.api.blending.dto.BlendingRequest;
import kr.blendit.api.blending.facade.BlendingFacade;
import kr.blendit.api.blending.service.BlendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class BlendingController {

    private final BlendingService blendingService;
    private final BlendingFacade blendingFacade;

    @PostMapping("/blending")
    public void create(@AuthenticationPrincipal String userUuid, @RequestBody BlendingRequest blendingRequest) {

        blendingService.create(userUuid, blendingRequest);
    }

    @DeleteMapping("/blending")
    public void delete(@AuthenticationPrincipal String userUuid, @RequestParam String blendingUuid) {

        blendingService.delete(userUuid, blendingUuid);
    }

    @PatchMapping("/blending/{blendingUuid}")
    public void update(@AuthenticationPrincipal String userUuid, @PathVariable String blendingUuid, @RequestBody BlendingRequest blendingRequest) {
        blendingService.update(userUuid, blendingUuid, blendingRequest);
    }
}
