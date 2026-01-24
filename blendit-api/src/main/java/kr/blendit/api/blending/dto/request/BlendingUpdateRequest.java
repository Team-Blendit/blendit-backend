package kr.blendit.api.blending.dto.request;

import kr.blendit.api.common.constant.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlendingUpdateRequest {

    private String title;
    private String content;
    private Position position;
    private List<String> keywords;
    private Integer capacity;
    private String region;
    private String openChattingUrl;
    private LocalDateTime schedule;
    private Boolean autoApproval;
}
