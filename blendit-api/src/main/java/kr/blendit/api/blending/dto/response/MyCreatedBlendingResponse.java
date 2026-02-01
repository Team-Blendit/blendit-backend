package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingStatus;
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
public class MyCreatedBlendingResponse {

  private String blendingUuid;
  private String title;
  private Position position;
  private List<String> keywords;
  private String region;
  private Integer currentUserCount;
  private LocalDateTime schedule;
  private String openChattingUrl;
  private BlendingStatus blendingStatus;
  private Boolean useFlag;


  public static List<MyCreatedBlendingResponse> listFrom(List<BlendingUser> blendingUsers, Map<Long, List<String>> keywordMap) {

    List<MyCreatedBlendingResponse> list = new ArrayList<>();

    for (BlendingUser blendingUser : blendingUsers) {

      Blending blending = blendingUser.getBlending();
      List<String> keywords = keywordMap.getOrDefault(blending.getId(), Collections.emptyList());

      MyCreatedBlendingResponse myCreatedBlendingResponse = MyCreatedBlendingResponse.builder()
              .blendingUuid(blending.getUuid())
              .title(blending.getTitle())
              .position(blending.getPosition())
              .keywords(keywords)
              .region(blending.getRegion())
              .currentUserCount(blending.getCurrentParticipantCount())
              .schedule(blending.getSchedule())
              .openChattingUrl(blending.getOpenChattingUrl())
              .blendingStatus(blending.getStatus())
              .useFlag(blending.isUseFlag())
              .build();

      list.add(myCreatedBlendingResponse);
    }

    return list;
  }
}
