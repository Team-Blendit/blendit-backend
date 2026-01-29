package kr.blendit.api.blending.service;

import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.blending.dto.response.BlendingListResponse;
import kr.blendit.api.blending.repository.BlendingBookmarkRepository;
import kr.blendit.api.blending.repository.BlendingRepository;
import kr.blendit.api.keyword.domain.Keyword;
import kr.blendit.api.user.domain.UserKeyword;
import kr.blendit.api.user.repository.UserKeywordRepository;
import kr.blendit.common.security.jwt.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlendingQueryService {

    private final BlendingRepository blendingRepository;
    private final BlendingBookmarkRepository blendingBookmarkRepository;
    private final UserKeywordRepository userKeywordRepository;

    /**
     * 블렌딩 목록 조회
     */
    public Page<BlendingListResponse> getList(
            CurrentUser currentUser, BlendingListRequest blendingListRequest, Pageable pageable) {

        String userUuid = (currentUser != null) ? currentUser.getUserUuid() : null;

        List<BlendingListResponse> finalBlendingList = new ArrayList<>();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        boolean isLogin = (userUuid != null && !userUuid.equals("anonymousUser"));

        // 추천 블렌딩 조회
        if (isLogin && pageNumber == 0) {
            int recommendationCount = 12;
            List<UserKeyword> keywords = userKeywordRepository.findUserKeywordNamesByUserUuid(userUuid);
            finalBlendingList.addAll(getRecommendationList(userUuid, keywords, recommendationCount));
            // todo: 추천 개수가 12개가 되지 않는다면 모자란 개수는 최근 블렌딩을 보여줘야한다.
        }

        int layoutDeduction = 4; // 한 줄에 표시할 블렌딩

        long offset = calculateOffset(isLogin, pageNumber, pageSize, layoutDeduction);
        int limit = calculateLimit(isLogin, pageNumber, pageSize, layoutDeduction);

        // 블렌딩 목록 조회
        if(limit > 0) {
            finalBlendingList.addAll(getBlendingList(userUuid, blendingListRequest, offset, limit, isLogin));
        }

        // 전체 개수 조회 및 계산
        long generalTotalCount = blendingRepository.countByCondition(userUuid, blendingListRequest);

        return new PageImpl<>(finalBlendingList, pageable, generalTotalCount);
    }


    /**
     * 추천 블렌딩 추출
     */
    private List<BlendingListResponse> getRecommendationList(
            String userUuid, List<UserKeyword> userKeywords, Integer recommendationCount) {

        List<String> keywords = UserKeyword.toNames(userKeywords);

        List<Blending> blendingList = blendingRepository.findRecommendation(keywords, recommendationCount);
        Set<Long> myBookmarkedIds = checkBookmark(userUuid, true, blendingList);
        return BlendingListResponse.listFrom(blendingList, myBookmarkedIds);
    }


    /**
     * 블렌딩 목록 추출 (검색어 및 필터 적용)
     */
    private List<BlendingListResponse> getBlendingList(
            String userUuid, BlendingListRequest blendingListRequest,
            long offset, int limit, boolean isLogin) {

        List<Blending> blendingList = blendingRepository.searchByCondition(userUuid, blendingListRequest, offset, limit);
        Set<Long> myBookmarkedIds = checkBookmark(userUuid, isLogin, blendingList);
        return BlendingListResponse.listFrom(blendingList, myBookmarkedIds);
    }


    /**
     * 현재 조회된 블렌딩 목록 중 사용자가 북마크한 bookmarkId 추출
     */
    private Set<Long> checkBookmark(String userUuid, boolean isLogin, List<Blending> blendingList) {
        Set<Long> myBookmarkedIds = new HashSet<>();
        if (isLogin && !blendingList.isEmpty()) {
            List<Long> blendingIds = blendingList.stream()
                    .map(Blending::getId)
                    .toList();

            myBookmarkedIds = blendingBookmarkRepository.findBookmarkedBlendingIds(userUuid, blendingIds);
        }

        return myBookmarkedIds;
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

}
