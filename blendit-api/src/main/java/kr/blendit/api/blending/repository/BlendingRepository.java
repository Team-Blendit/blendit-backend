package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlendingRepository extends JpaRepository<Blending, Long> {

    Optional<Blending> findByUuid(String uuid);

    @Query("""
            SELECT b
            FROM Blending b
                LEFT JOIN FETCH b.keywords bk
                LEFT JOIN FETCH bk.keyword
            WHERE b.uuid = :blendingUuid
            """)
    Optional<Blending> findByUuidWithKeywords(String blendingUuid);

    /**
     * BlendingUser 까지만 조회
     */
    @Query("""
            SELECT DISTINCT b
            FROM Blending b
                JOIN FETCH b.participants bp
            WHERE b.uuid = :uuid
                AND b.useFlag = true
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
                AND b.useFlag = true
            """)
    Optional<Blending> findByUuidWithUsers(String uuid);
}
