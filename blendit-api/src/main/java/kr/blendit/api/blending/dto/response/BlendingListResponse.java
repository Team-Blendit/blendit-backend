package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlendingListResponse {

    private String blendingUuid;
    private String title;
    private String hostNickname;
    private Experience hostExperience;
    // Todo: private String userProfileImage;
    private String region;
    private BlendingStatus blendingStatus;
    private Position position;
    private Integer capacity;
    private Integer currentUserCount;
    private List<String> keywords;
    private Boolean isBookmark;
    private Boolean isRecommended;


    public static List<BlendingListResponse> listFrom(
            List<Blending> blendingList,
            Set<Long> myBookmarkedBlendingIds,
            Map<Long, BlendingUser> hostMap,
            Boolean isRecommended) {

        List<BlendingListResponse> list = new ArrayList<>();

        for(Blending blending : blendingList) {
            BlendingListResponse blendingListResponse = BlendingListResponse.builder()
                    .blendingUuid(blending.getUuid())
                    .title(blending.getTitle())
                    .hostNickname(hostMap.get(blending.getId()).getUser().getNickname())
                    .hostExperience(hostMap.get(blending.getId()).getUser().getExperience())
                    .region(blending.getRegion())
                    .blendingStatus(blending.getStatus())
                    .position(blending.getPosition())
                    .capacity(blending.getCapacity())
                    .currentUserCount(blending.getCurrentParticipantCount())
                    .keywords(BlendingKeyword.extractKeywords(blending.getKeywords()))
                    .isBookmark(myBookmarkedBlendingIds.contains(blending.getId()))
                    .isRecommended(isRecommended)
                    .build();

            list.add(blendingListResponse);
        }

        return list;
    }

}
