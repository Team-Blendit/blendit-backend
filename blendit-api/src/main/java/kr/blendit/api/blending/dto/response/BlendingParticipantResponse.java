package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.BlendingUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlendingParticipantResponse {

    // Todo: 지수, 프로필사진, 직군, 연차, 지역, 키워드, 북마크 여부

    private String uuid;
    private BlendingGrade blendingGrade;
    private JoinStatus joinStatus;


    public static List<BlendingParticipantResponse> from(List<BlendingUser> blendingUsers) {
        List<BlendingParticipantResponse> blendingParticipantResponses = new ArrayList<>();

        for(BlendingUser blendingUser : blendingUsers) {
            BlendingParticipantResponse blendingUserResponse = BlendingParticipantResponse.builder()
                    .uuid(blendingUser.getUser().getUuid())
                    .blendingGrade(blendingUser.getBlendingGrade())
                    .joinStatus(blendingUser.getJoinStatus())
                    .build();
            blendingParticipantResponses.add(blendingUserResponse);
        }
        return blendingParticipantResponses;
    }


}
