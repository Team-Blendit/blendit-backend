package kr.blendit.api.blending.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.dto.request.BlendingListRequest;
import kr.blendit.api.common.constant.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

import static kr.blendit.api.blending.domain.QBlending.blending;
import static kr.blendit.api.blending.domain.QBlendingKeyword.blendingKeyword;
import static kr.blendit.api.blending.domain.QBlendingBookmark.blendingBookmark;
import static kr.blendit.api.common.domain.QKeyword.keyword;
import static kr.blendit.api.user.domain.QUser.user;

@RequiredArgsConstructor
public class BlendingRepositoryImpl implements BlendingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /**
     * 블렌딩 목록 조회 (검색어 및 필터링 적용)
     */
    @Override
    public List<Blending> searchByCondition(
            String userUuid, BlendingListRequest blendingListRequest, long offset, int limit) {

        return queryFactory
                .selectFrom(blending)
                .where(
                        equalPosition(blendingListRequest.getPosition()),
                        inRegions(blendingListRequest.getRegion()),
                        equalStatus(blendingListRequest.getIsRecruiting()),
                        containsKeywords(blendingListRequest.getKeywords()),
                        isBookmark(blendingListRequest.getIsBookmark(), userUuid),
                        containsSearchQuery(blendingListRequest.getQuery()),
                        blending.useFlag.isTrue()
                )
                .orderBy(blending.createdDate.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 추천 블렌딩 목록 조회 (UserKeyword와 일치하는 BlendingKeyword로 조회)
     */
    @Override
    public List<Blending> findRecommendation(List<String> keywords, Integer recommendationCount) {

        return queryFactory
                .selectFrom(blending)
                .where(
                        containsKeywords(keywords),
                        blending.status.eq(BlendingStatus.RECRUITING),
                        blending.useFlag.isTrue()
                )
                .limit(recommendationCount)
                .fetch();
    }

    /**
     * 조회한 목록 수 반환
     */
    @Override
    public long countByCondition(String userUuid, BlendingListRequest blendingListRequest) {
        Long count = queryFactory
                .select(blending.count())
                .from(blending)
                .where(
                        equalPosition(blendingListRequest.getPosition()),
                        inRegions(blendingListRequest.getRegion()),
                        equalStatus(blendingListRequest.getIsRecruiting()),
                        containsKeywords(blendingListRequest.getKeywords()),
                        isBookmark(blendingListRequest.getIsBookmark(), userUuid),
                        containsSearchQuery(blendingListRequest.getQuery()),
                        blending.useFlag.isTrue()
                )
                .fetchOne();

        return count != null ? count : 0L;
    }


    private BooleanExpression equalPosition(Position position) {
        return position != null ? blending.position.eq(position) : null;
    }

    private BooleanExpression inRegions(List<String> regions) {
        return regions != null && !regions.isEmpty() ? blending.region.in(regions) : null;
    }

    private BooleanExpression equalStatus(Boolean isRecruiting) {

        if(isRecruiting == null) return null;

        if(isRecruiting) {
            return blending.status.eq(BlendingStatus.RECRUITING);
        } else {
            return blending.status.eq(BlendingStatus.RECRUITMENT_CLOSED);
        }
    }

    private BooleanExpression containsKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return null;

        return JPAExpressions
                .selectOne()
                .from(blendingKeyword)
                .join(blendingKeyword.keyword, keyword)
                .where(blendingKeyword.blending.eq(blending), keyword.name.in(keywords))
                .exists();
    }

    /**
     * user가 blending을 북마크 했는지 여부
     */
    private BooleanExpression isBookmark(Boolean isBookmark, String userUuid) {
        if(isBookmark == null || !isBookmark || userUuid == null) return null;

        return JPAExpressions
                .selectOne()
                .from(blendingBookmark)
                .join(blendingBookmark.user, user)
                .where(
                        blendingBookmark.blending.eq(blending),
                        user.uuid.eq(userUuid)
                )
                .exists();
    }

    private BooleanExpression containsSearchQuery(String query) {
        if(!StringUtils.hasText(query)) return null;

        return blending.content.contains(query);
    }

}
