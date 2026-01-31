package kr.blendit.api.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;

public record UserOnboardingRequest(
    @NotNull
    Position position,

    @NotNull
    Experience experience,

    @Email
    @NotBlank
    String email,

    @NotBlank
    String province,

    @NotBlank
    String district,

    @NotNull
    @Size(min = 1, max = 3)
    List<String> keywordUuidList,

    @NotBlank
    String nickname
) {

}
