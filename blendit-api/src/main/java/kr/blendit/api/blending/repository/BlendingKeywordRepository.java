package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlendingKeywordRepository extends JpaRepository<BlendingKeyword, Long> {

  @Query("""
          SELECT bk
          FROM BlendingKeyword bk
            JOIN FETCH bk.keyword
          WHERE bk.blending IN :blendings
          """)
  List<BlendingKeyword> findAllByBlendingIdIn(List<Blending> blendings);
}
