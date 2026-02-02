package kr.blendit.api.user.repository;

import java.util.Optional;
import kr.blendit.api.user.constant.LoginType;
import kr.blendit.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

  Optional<User> findByUuid(String uuid);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUuid(String uuid);

  Optional<User> findByLoginTypeAndSocialCompanyUserId(LoginType loginType, String socialCompanyUserId);

  boolean existsByNickname(String nickname);
}
