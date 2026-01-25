package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingUserGrade;
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
    private BlendingUserGrade blendingUserGrade;
    private JoinStatus joinStatus;


    public static List<BlendingParticipantResponse> from(List<BlendingUser> blendingUsers, Boolean isHost) {
        List<BlendingParticipantResponse> blendingParticipantResponses = new ArrayList<>();

        for(BlendingUser blendingUser : blendingUsers) {

            if(canShow(blendingUser.getJoinStatus(), isHost)) {
                BlendingParticipantResponse blendingUserResponse = BlendingParticipantResponse.builder()
                        .uuid(blendingUser.getUser().getUuid())
                        .blendingUserGrade(blendingUser.getBlendingUserGrade())
                        .joinStatus(blendingUser.getJoinStatus())
                        .build();
                blendingParticipantResponses.add(blendingUserResponse);
            }
        }
        return blendingParticipantResponses;
    }


    /**
     * 블렌딩 권한에 따라 상태별로 유저를 보여줄지 결정하는 내부 메서드
     * @param isHost 현재 접속한 유저가 Host 인지 여부
     */
    private static boolean canShow(JoinStatus joinStatus, boolean isHost) {

        // 공통적으로 볼 수 있는 유저
        if(joinStatus == JoinStatus.APPROVED) {
            return true;
        }

        // Host만 볼 수 있는 유저
        if(isHost && joinStatus == JoinStatus.PENDING) {
            return true;
        }

        // REJECTED 상태는 모두 볼 수 없다.
        return false;
    }
}
