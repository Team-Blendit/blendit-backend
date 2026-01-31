package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.constant.Experience;
import kr.blendit.api.user.domain.UserKeyword;
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

  // Todo: 지수, 프로필사진, 북마크 여부,

  private String uuid;
  private String nickname;
  private String profileImage;
  private Position position;
  private Experience experience;
  private String province;
  private String district;
  private List<String> keywords;
  private BlendingUserGrade blendingUserGrade;
  private JoinStatus joinStatus;


  public static List<BlendingParticipantResponse> from(List<BlendingUser> blendingUsers) {
    // Todo: 북마크 여부 필요
    List<BlendingParticipantResponse> blendingParticipantResponses = new ArrayList<>();

    for (BlendingUser blendingUser : blendingUsers) {

      List<String> userKeywords = new ArrayList<>();
      for (UserKeyword userKeyword : blendingUser.getUser().getUserKeywords()) {
        userKeywords.add(userKeyword.getKeyword().getName());
      }

      BlendingParticipantResponse blendingUserResponse = BlendingParticipantResponse.builder()
              .uuid(blendingUser.getUser().getUuid())
              .nickname(blendingUser.getUser().getNickname())
              .profileImage(blendingUser.getUser().getProfileImage())
              .position(blendingUser.getUser().getPosition())
              .experience(blendingUser.getUser().getExperience())
              .province(blendingUser.getUser().getProvince())
              .district(blendingUser.getUser().getDistrict())
              .keywords(userKeywords)
              .blendingUserGrade(blendingUser.getBlendingUserGrade())
              .joinStatus(blendingUser.getJoinStatus())
              .build();
      blendingParticipantResponses.add(blendingUserResponse);

    }
    return blendingParticipantResponses;
  }

}
