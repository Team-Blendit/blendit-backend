package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.common.constant.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyAppliedBlendingResponse {

  private String blendingUuid;
  private String title;
  private Position position;
  private List<String> keywords;
  private String region;
  private Integer currentUserCount;
  private LocalDateTime schedule;
  private String openChattingUrl;
  private JoinStatus joinStatus;
  private Boolean useFlag;

  public static List<MyAppliedBlendingResponse> listFrom(List<BlendingUser> blendingUsers, Map<Long, List<String>> keywordMap) {

    List<MyAppliedBlendingResponse> list = new ArrayList<>();

    for (BlendingUser blendingUser : blendingUsers) {

      Blending blending = blendingUser.getBlending();
      List<String> keywords = keywordMap.getOrDefault(blending.getId(), Collections.emptyList());

      MyAppliedBlendingResponse myAppliedBlendingResponse = MyAppliedBlendingResponse.builder()
              .blendingUuid(blending.getUuid())
              .title(blending.getTitle())
              .position(blending.getPosition())
              .keywords(keywords)
              .region(blending.getRegion())
              .currentUserCount(blending.getCurrentParticipantCount())
              .schedule(blending.getSchedule())
              .openChattingUrl(blending.getOpenChattingUrl())
              .joinStatus(blendingUser.getJoinStatus())
              .useFlag(blending.isUseFlag())
              .build();

      list.add(myAppliedBlendingResponse);
    }

    return list;
  }
}




