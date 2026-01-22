package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlendingRepository extends JpaRepository<Blending, Long> {

    public Optional<Blending> findByUuid(String uuid);
}
