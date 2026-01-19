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
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlendingUser extends BaseTimeEntity {

    @Id
    @Column(name = "blending_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "blending_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Blending blending;

    @Column(name = "grade", length = 20)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "message")
    private String message;

    @Column(name = "join_status", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JoinStatus joinStatus = JoinStatus.PENDING;
}
