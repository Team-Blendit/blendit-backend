package kr.blendit.api.user.repository;

import java.util.Optional;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.domain.UserBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {

  boolean existsByUserAndBookmarkUser(User user, User bookmarkUser);

  Optional<UserBookmark> findByUserAndBookmarkUser(User user, User bookmarkUser);

  Page<UserBookmark> findAllByUserOrderByCreatedDateDesc(User user, Pageable pageable);
}
