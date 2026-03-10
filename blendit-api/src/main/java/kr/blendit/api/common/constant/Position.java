package kr.blendit.api.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Position {
  ALL("전체"),
  FRONTEND("프론트엔드"),
  BACKEND("백엔드"),
  PM("PM"),
  DESIGN("디자인"),
  MARKETING("마케팅"),
  DATA("데이터"),
  AI("AI"),
  SECURITY("보안");

  private final String description;
}