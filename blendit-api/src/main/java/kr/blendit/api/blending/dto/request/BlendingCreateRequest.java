package kr.blendit.api.blending.dto.request;

import jakarta.validation.constraints.*;
import kr.blendit.api.common.constant.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BlendingCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "포지션은 필수입니다.")
    private Position position;

    @NotNull(message = "모집 인원은 필수입니다.")
    @Min(value = 2, message = "모집 인원은 최소 2명 이상이어야 합니다.")
    private Integer capacity;

    @NotBlank(message = "지역 정보는 필수입니다.")
    private String region;

    private String openChattingUrl;

    @NotNull(message = "모임 일시는 필수입니다.")
    @Future(message = "모임 일시는 현재 시간보다 미래여야 합니다.")
    private LocalDateTime schedule;

    @NotNull(message = "자동 승인 여부는 필수입니다.")
    private Boolean autoApproval;

    @NotEmpty(message = "키워드는 최소 1개 이상이어야 합니다.")
    @Size(max = 3, message = "키워드는 최대 3개까지만 등록 가능합니다.")
    private List<String> keywords;
}
