package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    public Optional<Keyword> findByName(String name);
}
