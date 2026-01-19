package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.user.domain.User;
import lombok.*;

@Entity
@Table(
        name ="blending_bookmark",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blending_bookmark_blending_user",
                        columnNames = {"blending_id", "user_id"}
                )
        }
)
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlendingBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blending_bookmark_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @JoinColumn(name = "blending_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Blending blending;
}
