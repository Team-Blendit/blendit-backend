package kr.blendit.api.blending.repository;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.user.domain.User;
import org.hibernate.mapping.Join;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BlendingUserRepository extends JpaRepository<BlendingUser, Long>, BlendingUserRepositoryCustom {


    /**
     * 이 블렌딩에서 특정 권한의 유저 여부(주로 해당 유저가 HOST 등급인지 확인)
     *
     * @apiNote Blending만 조회하여 participants 내부는 프록시 객체일 경우 사용하기 적합합니다.
     */
    boolean existsByBlendingAndUser_UuidAndBlendingUserGrade(Blending blending, String userUuid, BlendingUserGrade grade);

    /**
     * 특정 상태의 블렌딩 유저 수 조회(참여 승인 및 Host 상태 추출 가능)
     *
     * @apiNote Blending만 조회하여 participants 내부는 프록시 객체일 경우 사용하기 적합합니다.
     */
    long countByBlendingAndJoinStatus(Blending blending, JoinStatus joinStatus);

    /**
     * 유저의 블렌딩 참여 여부 추출
     */
    Optional<BlendingUser> findByBlendingAndUser(Blending blending, User user);

    /**
     * 참여중인 특정 BlendingUser 조회
     */
    Optional<BlendingUser> findByBlendingAndUser_Uuid(Blending blending, String userUuid);

    @Query("""
            SELECT bu
            FROM BlendingUser bu
                JOIN FETCH bu.user u
                LEFT JOIN FETCH u.userKeywords uk
                LEFT JOIN FETCH uk.keyword
            WHERE bu.blending = :blending AND bu.joinStatus IN :joinStatuses
            """)
    List<BlendingUser> findByBlendingAndJoinStatusInWithUser(Blending blending, List<JoinStatus> joinStatuses);

    /**
     * 특정 권한이 아닌 BlendingUser 조회
     */
    @Query(value = """
            SELECT bu
            FROM BlendingUser bu
                JOIN FETCH bu.blending
            WHERE bu.user.uuid = :userUuid
                AND bu.blendingUserGrade != :blendingUserGrade
            """,
            countQuery = """
            SELECT count(bu)
            FROM BlendingUser bu
            WHERE bu.user.uuid = :userUuid
                AND bu.blendingUserGrade != :blendingUserGrade
            """)
    Page<BlendingUser> findAllByUserUuidAndNotBlendingUserGrade(
            String userUuid, BlendingUserGrade blendingUserGrade, Pageable pageable);


    /**
     * 특정 권한의 BlendingUser 조회
     */
    @Query(value = """
            SELECT bu
            FROM BlendingUser bu
                JOIN FETCH bu.blending b
            WHERE bu.user.uuid = :userUuid
                AND bu.blendingUserGrade = :blendingUserGrade
                AND b.useFlag = true
            """,
            countQuery = """
            SELECT count(bu)
            FROM BlendingUser bu
                JOIN bu.blending b
            WHERE bu.user.uuid = :userUuid
                AND bu.blendingUserGrade = :blendingUserGrade
                AND b.useFlag = true
            """)
    Page<BlendingUser> findAllByUserUuidAndBlendingUserGrade(
            String userUuid, BlendingUserGrade blendingUserGrade, Pageable pageable);
}
