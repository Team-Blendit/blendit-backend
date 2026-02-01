package kr.blendit.api.blending.service;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.response.MyAppliedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyCreatedBlendingResponse;
import kr.blendit.api.blending.dto.response.MyHistoryBlendingResponse;
import kr.blendit.api.blending.repository.BlendingKeywordRepository;
import kr.blendit.api.blending.repository.BlendingRepository;
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
  private final BlendingRepository blendingRepository;


  /**
   * 내가 신청한 블렌딩 목록 조회
   */
  public Page<MyAppliedBlendingResponse> getMyAppliedList(CurrentUser currentUser, Pageable pageable) {

    String userUuid = (currentUser != null) ? currentUser.getUserUuid() : null;

    Page<BlendingUser> blendingUsers =
            blendingUserRepository.findAllByUserUuidAndNotBlendingUserGrade(userUuid, BlendingUserGrade.HOST, pageable);

    if (blendingUsers.isEmpty()) {
      return Page.empty(pageable);
    }

    List<Blending> blendings = blendingUsers.getContent().stream()
            .map(BlendingUser::getBlending)
            .toList();

    Map<Long, List<String>> keywordMap = getKeywordMap(blendings);

    List<MyAppliedBlendingResponse> myAppliedBlendingResponseList =
            MyAppliedBlendingResponse.listFrom(blendingUsers.getContent(), keywordMap);

    return new PageImpl<>(myAppliedBlendingResponseList, pageable, blendingUsers.getTotalElements());
  }


  /**
   * 내가 생성한 블렌딩 목록 조회
   */
  public Page<MyCreatedBlendingResponse> getMyCreatedList(CurrentUser currentUser, Pageable pageable) {

    String userUuid = (currentUser != null) ? currentUser.getUserUuid() : null;

    Page<BlendingUser> blendingUsers =
            blendingUserRepository.findAllByUserUuidAndBlendingUserGrade(userUuid, BlendingUserGrade.HOST, pageable);

    if (blendingUsers.isEmpty()) {
      return Page.empty(pageable);
    }

    List<Blending> blendings = blendingUsers.getContent().stream()
            .map(BlendingUser::getBlending)
            .toList();

    Map<Long, List<String>> keywordMap = getKeywordMap(blendings);

    List<MyCreatedBlendingResponse> myCreatedBlendingResponseList =
            MyCreatedBlendingResponse.listFrom(blendingUsers.getContent(), keywordMap);

    return new PageImpl<>(myCreatedBlendingResponseList, pageable, blendingUsers.getTotalElements());
  }


  /**
   * 나의 활동 내역 조회
   */
  public Page<MyHistoryBlendingResponse> getMyHistoryList(CurrentUser currentUser, Pageable pageable) {

    String userUuid = (currentUser != null) ? currentUser.getUserUuid() : null;

    Page<Blending> blendings =
            blendingRepository.findAllByUserUuidAnBlendingStatus(userUuid, BlendingStatus.COMPLETED, pageable);

    if (blendings.isEmpty()) {
      return Page.empty(pageable);
    }

    Map<Long, List<String>> keywordMap = getKeywordMap(blendings.getContent());

    List<MyHistoryBlendingResponse> myHistoryBlendingResponses =
            MyHistoryBlendingResponse.listFrom(blendings.getContent(), keywordMap);

    return new PageImpl<>(myHistoryBlendingResponses, pageable, blendings.getTotalElements());
  }


  /**
   * 블렌딩의 keyword List 조회
   */
  private Map<Long, List<String>> getKeywordMap(List<Blending> blendings) {
    List<BlendingKeyword> blendingKeywordList = blendingKeywordRepository.findAllByBlendingIdIn(blendings);

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
