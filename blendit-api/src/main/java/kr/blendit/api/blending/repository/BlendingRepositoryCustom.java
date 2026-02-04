package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlendingRepositoryCustom {

    Page<Blending> searchByCondition(String userUuid, BlendingListRequest blendingListRequest, Pageable pageable);

    List<Blending> findRecommendation(List<String> keywords, Integer recommendationCount);

    long countByCondition(String userUuid, BlendingListRequest blendingListRequest);

    List<Blending> findRecent(Integer getCount, List<Long> excludeIds);
}
