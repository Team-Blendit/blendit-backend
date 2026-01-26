package kr.blendit.api.keyword.service;

import java.util.List;
import kr.blendit.api.keyword.controller.dto.KeywordListResponse;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.keyword.repository.KeywordRepository;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

  private final KeywordRepository keywordRepository;

  public List<Keyword> getKeywordList(List<String> keywordUuidList) {
    List<Keyword> keywordList = keywordRepository.findAllByUuidIn(keywordUuidList);
    if (keywordList.size() != keywordUuidList.size()) {
      throw new BaseException(BaseErrorCode.KEYWORD_NOT_FOUND);
    }
    return keywordList;
  }

  public List<KeywordListResponse> getKeywordList() {
    List<Keyword> keywordList = keywordRepository.findAll();
    return keywordList.stream()
        .map(KeywordListResponse::create)
        .toList();
  }
}
