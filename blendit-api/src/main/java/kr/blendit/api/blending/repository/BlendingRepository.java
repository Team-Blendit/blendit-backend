package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlendingRepository extends JpaRepository<Blending, Long> {

    Optional<Blending> findByUuid(String uuid);

    /**
     * BlendingUser 까지만 조회
     */
    @Query("""
            SELECT DISTINCT b
            FROM Blending b
                JOIN FETCH b.participants bp
            WHERE b.uuid = :uuid
            """)
    Optional<Blending> findByUuidWithParticipants(String uuid);


    /**
     * 참여자 정보까지 함께 조회
     */
    @Query("""
            SELECT DISTINCT b
            FROM Blending b
                JOIN FETCH b.participants bp
                JOIN FETCH bp.user u
            WHERE b.uuid = :uuid
            """) // Todo: 조회 중인 유저가 Host 라면 모든 블렌딩 유저 응답 / MEMBER 라면 참여 중인 블렌딩 유저만 응답하도록 동적 쿼리 구성 필요
    Optional<Blending> findByUuidWithUsers(String uuid);
}
