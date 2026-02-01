package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.common.constant.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
}
