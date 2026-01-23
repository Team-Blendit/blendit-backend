package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlendingRepository extends JpaRepository<Blending, Long> {

    Optional<Blending> findByUuid(String uuid);

    @Query("""
            SELECT DISTINCT b
            FROM Blending b
                JOIN FETCH b.participants bp
                JOIN FETCH bp.user u
            WHERE b.uuid = :uuid
            """)
    Optional<Blending> findByUuidWithParticipants(String uuid);
}
