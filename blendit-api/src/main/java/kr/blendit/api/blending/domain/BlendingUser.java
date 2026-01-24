package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.Grade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.user.domain.User;
import kr.blendit.common.entity.BaseTimeEntity;
import lombok.*;

@Entity
@Table(
        name ="blending_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blending_user_blending_user",
                        columnNames = {"blending_id", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlendingUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "blending_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Blending blending;

    @Column(name = "grade", length = 20)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "message")
    private String message;

    @Column(name = "join_status", length = 20)
    @Enumerated(EnumType.STRING)
    private JoinStatus joinStatus;


    /**
     * 블렌딩 유저 생성자
     */
    @Builder(access = AccessLevel.PRIVATE)
    private BlendingUser(User user, Blending blending, Grade grade, String message, JoinStatus joinStatus) {
        this.user = user;
        this.blending = blending;
        this.grade = grade;
        this.message = message;
        this.joinStatus = joinStatus;
    }


    /**
     * 블렌딩 유저 생성
     */
    public static BlendingUser create(User user, Blending blending, Grade grade, String message, JoinStatus joinStatus) {
        // Todo: null 체크

        return BlendingUser.builder()
                .user(user)
                .blending(blending)
                .grade(grade)
                .message(message)
                .joinStatus(joinStatus)
                .build();
    }
}
