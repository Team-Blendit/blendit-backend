package kr.blendit.api.user.repository;

import kr.blendit.api.user.domain.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

  /**
   * 유저 한 명의 UserKeyword List 조회
   * @param userUuid
   * @return
   */
  @Query("""
          SELECT uk
          FROM UserKeyword uk
            JOIN FETCH uk.keyword
          WHERE uk.user.uuid = :userUuid
          """)
  List<UserKeyword> findUserKeywordNamesByUserUuid(String userUuid);
}
