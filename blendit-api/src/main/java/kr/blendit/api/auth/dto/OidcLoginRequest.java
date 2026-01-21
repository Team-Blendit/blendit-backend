package kr.blendit.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OidcLoginRequest(
        @NotBlank(message = "인가 코드는 필수입니다.")
        String code
) {}
