package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.constant.Grade;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BlendingUserRepository extends JpaRepository<BlendingUser, Long> {

    public Optional<BlendingUser> findByBlendingAndGrade(Blending blending, Grade grade);
}
