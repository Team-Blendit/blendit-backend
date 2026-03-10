package kr.blendit.api.blending.service;

import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.blending.dto.response.BlendingListResponse;
import kr.blendit.api.blending.repository.BlendingBookmarkRepository;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.blending.repository.BlendingUserRepository;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlendingQueryService {

  private final BlendingRepository blendingRepository;
  private final BlendingBookmarkRepository blendingBookmarkRepository;
  private final BlendingUserRepository blendingUserRepository;

  /**
   * 블렌딩 목록 조회
   */
  public Page<BlendingListResponse> getList(
          CurrentUser currentUser, BlendingListRequest blendingListRequest, Pageable pageable) {

    String userUuid = (currentUser != null) ? currentUser.getUserUuid() : null;
    boolean isLogin = (userUuid != null);

    Page<Blending> blendingPage = blendingRepository.searchByCondition(userUuid, blendingListRequest, pageable);
    Set<Long> myBookmarkedBlendingIds = checkBookmark(userUuid, isLogin, blendingPage);

    Map<Long, BlendingUser> hostMap = convertHostMap(blendingPage);

    return BlendingListResponse.listFrom(blendingPage, myBookmarkedBlendingIds, hostMap, false);
  }


  /**
   * 블렌딩 목록에서 Host의 PK를 추출하여 Map으로 변환
   */
  private Map<Long, BlendingUser> convertHostMap(Page<Blending> blendingPage) {

    List<Long> blendingIds = blendingPage.getContent().stream()
            .map(Blending::getId)
            .toList();

    Map<Long, BlendingUser> hostMap = new HashMap<>();
    List<BlendingUser> hosts = blendingUserRepository.findHostsByBlendingIds(blendingIds);

    for(BlendingUser blendingUser : hosts) {
      hostMap.putIfAbsent(blendingUser.getBlending().getId(), blendingUser);
    }

    return hostMap;
  }


  /**
   * 현재 조회된 블렌딩 목록 중 사용자가 북마크한 bookmarkId 추출
   */
  private Set<Long> checkBookmark(String userUuid, boolean isLogin, Page<Blending> blendingPage) {
    Set<Long> myBookmarkedBlendingIds = new HashSet<>();
    if (isLogin && !blendingPage.getContent().isEmpty()) {
      List<Long> blendingIds = blendingPage.getContent().stream()
              .map(Blending::getId)
              .toList();

      myBookmarkedBlendingIds = blendingBookmarkRepository.findBookmarkedBlendingIds(userUuid, blendingIds);
    }

    return myBookmarkedBlendingIds;
  }


  /**
   * offset 계산
   */
  private long calculateOffset(boolean isLogin, int page, int size, int layoutDeduction) {

    if (!isLogin) return (long) page * size;

    if (page == 0) return 0;

    // 첫 페이지의 추천 블렌딩 고려
    int firstPageGeneralCount = size - layoutDeduction;
    return firstPageGeneralCount + (long) (page - 1) * size;
  }


  /**
   * limit 계산
   */
  private int calculateLimit(boolean isLogin, int page, int size, int layoutDeduction) {

    // 첫 페이지의 추천 블렌딩 고려
    if (isLogin && page == 0) return Math.max(0, size - layoutDeduction);
    return size;
  }


  /**
   * PageImpl의 자동 Total 보정 로직을 무시하고 실제 Total Count를 반환하는 커스텀 Page 생성
   */
  private <T> Page<T> createFixedPage(List<T> content, Pageable pageable, long fixedTotal) {
    return new PageImpl<>(content, pageable, fixedTotal) {

      @Override
      public long getTotalElements() {
        return fixedTotal; // 매개변수 fixedTotal를 강제로 반환한다.
      }

      @Override
      public int getTotalPages() {
        if (getSize() == 0) {
          return 1;
        }

        // 조정된 fixedTotal를 통해 TotalPage를 계산한다.
        return (int) Math.ceil((double) fixedTotal / getSize());
      }
    };
  }



  //  /**
//   * 추천 블렌딩 조회
//   */
//  private List<BlendingListResponse> getRecommendationList(String userUuid, int targetCount) {
//    List<UserKeyword> userKeywords = userKeywordRepository.findUserKeywordNamesByUserUuid(userUuid);
//    List<String> userKeywordNames = UserKeyword.toNames(userKeywords);
//
//    List<Blending> recommendationBlendings = blendingRepository.findRecommendation(userKeywordNames, targetCount);
//
//    if (recommendationBlendings.size() < targetCount) {
//      recommendationBlendings.addAll(getRecent(targetCount, recommendationBlendings));
//    }
//
//    return convertRecommendationList(userUuid, recommendationBlendings);
//  }
//
//
//  /**
//   * 추가 블렌딩 조회
//   *
//   * @apiNote 조회한 추천 블렌딩이 12개 미만일 경우 추가 조회 용도 (이미 조회한 추천 블렌딩을 제외하고 조회)
//   */
//  private List<Blending> getRecent(int targetCount, List<Blending> recommendationBlendings) {
//
//    int neededCount = targetCount - recommendationBlendings.size();
//    List<Long> existingIds = recommendationBlendings.stream()
//            .map(Blending::getId)
//            .toList();
//
//    return blendingRepository.findRecent(neededCount, existingIds);
//  }
//
//
//  /**
//   * 추천 블렌딩 변환
//   */
//  private List<BlendingListResponse> convertRecommendationList(
//          String userUuid, List<Blending> recommendationBlendings) {
//
//    Set<Long> myBookmarkedBlendingIds = checkBookmark(userUuid, true, recommendationBlendings);
//    Map<Long, BlendingUser> hostMap = convertHostMap(recommendationBlendings);
//
//    return BlendingListResponse.listFrom(recommendationBlendings, myBookmarkedBlendingIds, hostMap, true);
//  }

}
