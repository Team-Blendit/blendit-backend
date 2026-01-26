package kr.blendit.api.user.repository;

import kr.blendit.api.user.constant.LoginType;
import kr.blendit.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUuid(String uuid);

    Optional<User> findByLoginTypeAndSocialCompanyUserId(LoginType loginType, String socialCompanyUserId);
}
