package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    /**
     * 키워드 조회
     */
    public List<Keyword> findAllByNameIn(List<String> names);
}
