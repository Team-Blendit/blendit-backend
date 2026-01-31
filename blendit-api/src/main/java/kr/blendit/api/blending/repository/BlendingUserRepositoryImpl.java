package kr.blendit.api.blending.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.domain.BlendingUser;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.blendit.api.user.domain.QUser.user;
import static kr.blendit.api.blending.domain.QBlendingUser.blendingUser;

@RequiredArgsConstructor
public class BlendingUserRepositoryImpl implements BlendingUserRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * 각 블렌딩 참여자로 host 목록 조회
   */
  @Override
  public List<BlendingUser> findHostsByBlendingIds(List<Long> blendingIds) {
    return queryFactory
            .selectFrom(blendingUser)
            .join(blendingUser.user, user).fetchJoin()
            .where(
                    blendingUser.blending.id.in(blendingIds),
                    blendingUser.blendingUserGrade.eq(BlendingUserGrade.HOST)
            )
            .fetch();
  }
}
