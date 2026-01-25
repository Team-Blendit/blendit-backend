package kr.blendit.api.blending.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import kr.blendit.api.common.constant.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BlendingUpdateRequest {

    @Size(min = 1, message = "제목은 최소 1글자 이상이어야 합니다.")
    private String title;

    @Size(min = 1, message = "내용은 최소 1글자 이상이어야 합니다.")
    private String content;

    private Position position;

    @Size(max = 3, message = "키워드는 최대 3개까지만 등록 가능합니다.")
    private List<String> keywords;

    @Min(value = 2, message = "모집 인원은 최소 2명 이상이어야 합니다.")
    private Integer capacity;

    private String region;

    private String openChattingUrl;

    @Future(message = "모임 일시는 현재 시간보다 미래여야 합니다.")
    private LocalDateTime schedule;

    private Boolean autoApproval;
}
