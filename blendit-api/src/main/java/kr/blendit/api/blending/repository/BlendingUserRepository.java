package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BlendingUserRepository extends JpaRepository<BlendingUser, Long> {

    /**
     * 특정 권한의 블렌딩 유저 조회
     *
     * @apiNote BlendingService.validateHostPermission() 권한 검증 용도
     */
    public Optional<BlendingUser> findByBlendingAndBlendingGrade(Blending blending, BlendingGrade blendingGrade);
}
