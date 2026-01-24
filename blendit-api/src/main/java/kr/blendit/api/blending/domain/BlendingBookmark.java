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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlendingBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @JoinColumn(name = "blending_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Blending blending;

    @Builder(access = AccessLevel.PRIVATE)
    private BlendingBookmark(User user, Blending blending) {
        this.user = user;
        this.blending = blending;
    }

    public static BlendingBookmark create(User user, Blending blending) {
        // Todo: null 체크

        return BlendingBookmark.builder()
                .user(user)
                .blending(blending)
                .build();
    }
}
