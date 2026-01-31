package kr.blendit.api.keyword.controller.dto;

import kr.blendit.api.keyword.domain.Keyword;

public record KeywordListResponse(
    String uuid,
    String name
) {

  public static KeywordListResponse create(Keyword keyword) {
    return new KeywordListResponse(
        keyword.getUuid(),
        keyword.getName()
    );
  }
}
