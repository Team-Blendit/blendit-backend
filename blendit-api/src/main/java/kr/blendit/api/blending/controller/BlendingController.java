package kr.blendit.api.blending.controller;

import kr.blendit.api.blending.dto.BlendingRequest;
import kr.blendit.api.blending.service.BlendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class BlendingController {

    private final BlendingService blendingService;

    @PostMapping("/blending")
    public void create(@AuthenticationPrincipal String userUuid, BlendingRequest blendingRequest) {

        blendingService.create(userUuid, blendingRequest);
    }
}
