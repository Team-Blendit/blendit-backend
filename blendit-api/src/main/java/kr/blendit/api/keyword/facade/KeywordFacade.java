package kr.blendit.api.keyword.facade;

import java.util.List;
import kr.blendit.api.keyword.controller.dto.KeywordListResponse;
import kr.blendit.api.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeywordFacade {

  private final KeywordService keywordService;

  @Transactional(readOnly = true)
  public List<KeywordListResponse> getKeywordList() {
    return keywordService.getKeywordList();
  }

  @Transactional
  public void saveKeyword(String name) {
    keywordService.saveKeyword(name);
  }
}
