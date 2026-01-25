package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BlendingUserRepository extends JpaRepository<BlendingUser, Long> {

    /**
     * 특정 권한의 블렌딩 유저 조회
     *
     * @apiNote BlendingService.validateHostPermission() 권한 검증 용도
     */
    Optional<BlendingUser> findByBlendingAndBlendingGrade(Blending blending, BlendingGrade blendingGrade);

    /**
     * 참여 승인 및 Host 상태의 블렌딩 유저 수 조회
     *
     * @apiNote Blending만 조회하여 participants 내부는 프록시 객체일 경우 사용하기 적합합니다.
     */
    long countByBlendingAndJoinStatusIn(Blending blending, List<JoinStatus> statuses);

    /**
     * 유저의 블렌딩 참여 여부 추출(모든 JoinStatus 포함)
     */
    boolean existsByBlendingAndUser(Blending blending, User user);

    /**
     * 참여중인 특정 BlendingUser 조회
     */
    Optional<BlendingUser> findByBlendingAndUser(Blending blending, User user);
}
