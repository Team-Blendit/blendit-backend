package kr.blendit.api.user.repository;

import java.util.List;
import java.util.Optional;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.user.domain.UserBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {

  boolean existsByUserAndBookmarkUser(User user, User bookmarkUser);

  Optional<UserBookmark> findByUserAndBookmarkUser(User user, User bookmarkUser);

  Page<UserBookmark> findAllByUserOrderByCreatedDateDesc(User user, Pageable pageable);

  /**
   * 유저 List에서 현재 유저가 북마크하고 있는 유저 PK 조회
   */
  @Query("SELECT ub.bookmarkUser.id " +
          "FROM UserBookmark ub " +
          "WHERE ub.user.uuid = :currentUserUuid " +
          "AND ub.bookmarkUser IN :targetUsers")
  List<Long> findBookmarkedUserIds(String currentUserUuid, List<User> targetUsers);
}
