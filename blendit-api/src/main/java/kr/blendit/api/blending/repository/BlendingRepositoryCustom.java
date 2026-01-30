package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.blending.dto.response.BlendingListResponse;

import java.util.List;

public interface BlendingRepositoryCustom {

    List<Blending> searchByCondition(String userUuid, BlendingListRequest blendingListRequest, long offset, int limit);

    List<Blending> findRecommendation(List<String> keywords, Integer recommendationCount);

    long countByCondition(String userUuid, BlendingListRequest blendingListRequest);

    List<Blending> findRecent(Integer getCount, List<Long> excludeIds);
}
