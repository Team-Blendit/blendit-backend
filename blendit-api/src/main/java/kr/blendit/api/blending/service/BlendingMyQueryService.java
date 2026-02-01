package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.domain.BlendingKeyword;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.response.MyAppliedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyCreatedBlendingResponse;
import kr.blendit.api.blending.repository.BlendingKeywordRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlendingMyQueryService {

  private final BlendingUserRepository blendingUserRepository;
  private final BlendingKeywordRepository blendingKeywordRepository;

  public Page<MyAppliedBlendingResponse> getMyAppliedList(CurrentUser currentUser, Pageable pageable) {

    String userUuid = (currentUser != null) ? currentUser.getUserUuid() : null;

    Page<BlendingUser> blendingUsers =
            blendingUserRepository.findAllByUserUuidAndBlendingUserGrade(userUuid, BlendingUserGrade.HOST, pageable);

    if (blendingUsers.isEmpty()) {
      return Page.empty(pageable);
    }

    List<Long> blendingIds = blendingUsers.getContent().stream()
            .map(bu -> bu.getBlending().getId())
            .toList();

    Map<Long, List<String>> keywordMap = getKeywordMap(blendingIds);

    List<MyAppliedBlendingResponse> myAppliedBlendingResponseList =
            MyAppliedBlendingResponse.listFrom(blendingUsers.getContent(), keywordMap);

    return new PageImpl<>(myAppliedBlendingResponseList, pageable, blendingUsers.getTotalElements());
    // joinStatus에 취소 상태도 추가해서 보여줘야한다. (신청 취소 시 삭제가 아니라 취소 상태로 변경해야함)

    // API를 하나 더 만들어서 취소 상태인 블렌딩 흔적을 아예 삭제 (BlendingUser 데이터)할 수 있어야 한다.
  }


  public Page<MyCreatedBlendingResponse> getMyCreatedList(CurrentUser currentUser, Pageable pageable) {
    return null;
  }


  private Map<Long, List<String>> getKeywordMap(List<Long> blendingIds) {
    List<BlendingKeyword> blendingKeywordList = blendingKeywordRepository.findAllByBlendingIdIn(blendingIds);

    return blendingKeywordList.stream()
            .collect(Collectors.groupingBy(
                    bk -> bk.getBlending().getId(),
                    Collectors.mapping(
                            bk -> bk.getKeyword().getName(),
                            Collectors.toList()
                    )
            ));
  }
}
