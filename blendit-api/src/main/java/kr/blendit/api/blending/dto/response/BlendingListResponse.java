package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import kr.blendit.api.common.constant.Position;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlendingListResponse {

    private String blendingUuid;
    private String title;
    // Todo: private String userName;
    // Todo: private String userProfileImage;
    // Todo: private 연차?
    private String region;
    private BlendingStatus blendingStatus;
    private Position position;
    private Integer capacity;
    private Integer currentUserCount;
    private List<String> keywords;
    private Boolean isBookmark;


    public static List<BlendingListResponse> listFrom(
            List<Blending> blendingList,
            Set<Long> myBookmarkedIds) {

        List<BlendingListResponse> list = new ArrayList<>();

        for(Blending blending : blendingList) {
            BlendingListResponse blendingListResponse = BlendingListResponse.builder()
                    .blendingUuid(blending.getUuid())
                    .title(blending.getTitle())
                    .region(blending.getRegion())
                    .blendingStatus(blending.getStatus())
                    .position(blending.getPosition())
                    .capacity(blending.getCapacity())
                    .currentUserCount(blending.getCurrentParticipantCount())
                    .keywords(BlendingKeyword.extractKeywords(blending.getKeywords()))
                    .isBookmark(myBookmarkedIds.contains(blending.getId()))
                    .build();

            list.add(blendingListResponse);
        }

        return list;
    }

}
