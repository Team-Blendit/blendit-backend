package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlendingBookmarkRepository extends JpaRepository<BlendingBookmark, Long> {

    /**
     * 해당 블렌딩의 북마크 수 조회
     */
    long countByBlending(Blending blending);

    /**
     * 요청한 유저가 해당 블렌딩을 북마크했는지 여부
     */
    boolean existsByBlendingAndUser_Uuid(Blending blending, String userUuid);
}
