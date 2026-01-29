package kr.blendit.api.user.repository;

import kr.blendit.api.user.domain.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

}
