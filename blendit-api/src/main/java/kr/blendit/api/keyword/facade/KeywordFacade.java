package kr.blendit.api.keyword.facade;

import java.util.List;
import kr.blendit.api.keyword.controller.dto.KeywordListResponse;
import kr.blendit.api.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordFacade {

  private final KeywordService keywordService;

  public List<KeywordListResponse> getKeywordList() {
    return keywordService.getKeywordList();
  }
}
