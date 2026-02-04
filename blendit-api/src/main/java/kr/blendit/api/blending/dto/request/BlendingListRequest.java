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
    private List<String> keywordUuidList;
    private List<String> region;
    private Integer capacity;
    private Boolean isRecruiting;
    private Boolean isBookmark;
    private String query;
}
