package kr.blendit.api.blending.dto.request;

import kr.blendit.api.common.constant.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class BlendingListRequest {

    private Position position;
    private List<String> keywords;
    private List<String> region;
    private Boolean isRecruiting;
    private Boolean isBookmark;
    private String query;
}
