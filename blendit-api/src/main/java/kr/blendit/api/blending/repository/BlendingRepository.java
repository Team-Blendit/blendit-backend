package kr.blendit.api.blending.repository;

import jakarta.persistence.LockModeType;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlendingRepository extends JpaRepository<Blending, Long>, BlendingRepositoryCustom {

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


    /**
     * 유저가 참여하고 있는 특정 상태의 Blending 조회
     */
    @Query(value = """
            SELECT b
            FROM Blending b
                JOIN FETCH b.participants bu
            WHERE bu.user.uuid = :userUuid
                AND b.status = :blendingStatus
            """,
            countQuery = """
            SELECT count(b)
            FROM Blending b
                JOIN b.participants bu
            WHERE bu.user.uuid = :userUuid
                AND b.status = :blendingStatus
            """)
    Page<Blending> findAllByUserUuidAnBlendingStatus(
            String userUuid, BlendingStatus blendingStatus, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM Blending b
            WHERE b.uuid = :uuid
            """)
    Optional<Blending> findByUuidLock(String uuid);
}
