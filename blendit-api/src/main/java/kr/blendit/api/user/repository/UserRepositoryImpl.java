package kr.blendit.api.user.repository;

import static kr.blendit.api.keyword.domain.QKeyword.keyword;
import static kr.blendit.api.user.domain.QUser.user;
import static kr.blendit.api.user.domain.QUserBookmark.userBookmark;
import static kr.blendit.api.user.domain.QUserKeyword.userKeyword;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.blendit.api.common.constant.Position;
import kr.blendit.api.user.controller.dto.GetUserSearchRequest;
import kr.blendit.api.user.controller.dto.GetUserSearchResponse;
import kr.blendit.api.user.domain.QUser;
import kr.blendit.api.user.repository.dto.UserSearchProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<GetUserSearchResponse> searchUsers(GetUserSearchRequest request, String currentUserUuid, Pageable pageable) {
    QUser bookmarkerUser = new QUser("bookmarkerUser");
    QUser myUser = new QUser("myUser");

    // 1. User 기본 정보 + 북마크 여부 조회
    List<UserSearchProjection> userContent = queryFactory
        .select(Projections.constructor(UserSearchProjection.class,
            user.uuid,
            user.nickname,
            user.position,
            user.experience,
            user.province,
            user.district,
            user.profileImage,
            currentUserUuid != null
                ? JPAExpressions.selectOne()
                .from(userBookmark)
                .join(userBookmark.bookmarkUser, bookmarkerUser)
                .join(userBookmark.user, myUser)
                .where(
                    bookmarkerUser.eq(user),
                    myUser.uuid.eq(currentUserUuid)
                )
                .exists()
                : Expressions.asBoolean(false)
        ))
        .from(user)
        .where(
            user.useFlag.isTrue(),
            isOnboardingComplete(),
            positionEq(request.position()),
            districtIn(request.districtList()),
            keywordUuidIn(request.keywordUuidList()),
            bookmarkCondition(request.isBookmarked(), currentUserUuid)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(user.createdDate.desc())
        .fetch();

    if (userContent.isEmpty()) {
      return Page.empty(pageable);
    }

    // 2. User UUID 목록으로 키워드 조회
    List<String> userUuids = userContent.stream()
        .map(UserSearchProjection::userUuid)
        .toList();

    Map<String, List<String>> keywordMap = queryFactory
        .select(user.uuid, keyword.name)
        .from(userKeyword)
        .join(userKeyword.user, user)
        .join(userKeyword.keyword, keyword)
        .where(user.uuid.in(userUuids))
        .fetch()
        .stream()
        .collect(Collectors.groupingBy(
            tuple -> tuple.get(user.uuid),
            Collectors.mapping(tuple -> tuple.get(keyword.name), Collectors.toList())
        ));

    // 3. 최종 Response 변환
    List<GetUserSearchResponse> content = userContent.stream()
        .map(dto -> new GetUserSearchResponse(
            dto.userUuid(),
            dto.nickname(),
            dto.position(),
            dto.experience(),
            dto.province(),
            dto.district(),
            dto.profileImageUrl(),
            keywordMap.getOrDefault(dto.userUuid(), List.of()),
            dto.isBookmarked()
        ))
        .toList();

    // 4. Count 쿼리
    JPAQuery<Long> countQuery = queryFactory
        .select(user.count())
        .from(user)
        .where(
            user.useFlag.isTrue(),
            isOnboardingComplete(),
            positionEq(request.position()),
            districtIn(request.districtList()),
            keywordUuidIn(request.keywordUuidList()),
            bookmarkCondition(request.isBookmarked(), currentUserUuid)
        );

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanExpression isOnboardingComplete() {
    return JPAExpressions.selectOne()
        .from(userKeyword)
        .where(userKeyword.user.eq(user))
        .exists();
  }

  private BooleanExpression positionEq(Position position) {
    return position != null ? user.position.eq(position) : null;
  }

  private BooleanExpression districtIn(List<String> districtList) {
    return !CollectionUtils.isEmpty(districtList) ? user.district.in(districtList) : null;
  }

  private BooleanExpression keywordUuidIn(List<String> keywordUuidList) {
    if (CollectionUtils.isEmpty(keywordUuidList)) {
      return null;
    }
    return JPAExpressions.selectOne()
        .from(userKeyword)
        .join(userKeyword.keyword, keyword)
        .where(
            userKeyword.user.eq(user),
            keyword.uuid.in(keywordUuidList)
        )
        .exists();
  }

  private BooleanExpression bookmarkCondition(Boolean isBookmarked, String currentUserUuid) {
    if (!StringUtils.hasText(currentUserUuid) || isBookmarked == null || !isBookmarked) {
      return null;
    }

    QUser bookmarkerUser = new QUser("bookmarkerUserCondition");
    QUser myUser = new QUser("myUserCondition");

    return JPAExpressions.selectOne()
        .from(userBookmark)
        .join(userBookmark.bookmarkUser, bookmarkerUser)
        .join(userBookmark.user, myUser)
        .where(
            bookmarkerUser.eq(user),
            myUser.uuid.eq(currentUserUuid)
        )
        .exists();
  }
}
