package kr.blendit.api.keyword.repository;

import java.util.List;
import kr.blendit.api.keyword.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  List<Keyword> findAllByUuidIn(List<String> keywordUuidList);

  Boolean existsByName(String name);

}
