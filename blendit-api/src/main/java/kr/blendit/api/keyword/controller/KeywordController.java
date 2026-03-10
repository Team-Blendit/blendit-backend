package kr.blendit.api.keyword.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.blendit.api.keyword.controller.dto.KeywordListResponse;
import kr.blendit.api.keyword.facade.KeywordFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Keyword API")
@RestController
@RequestMapping("/api/blendit/keyword")
@RequiredArgsConstructor
public class KeywordController {

  private final KeywordFacade keywordFacade;

  @Operation(summary = "키워드 리스트 조회")
  @GetMapping
  public List<KeywordListResponse> getKeywordList() {
    return keywordFacade.getKeywordList();
  }

  @Operation(summary = "키워드 생성", description = "테스트용입니다.")
  @PostMapping
  public void addKeyword(String keyword) {
    keywordFacade.saveKeyword(keyword);
  }

}
