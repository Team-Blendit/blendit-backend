package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.BlendingUser;

import java.util.List;

public interface BlendingUserRepositoryCustom {

  List<BlendingUser> findHostsByBlendingIds(List<Long> blendingIds);
}
