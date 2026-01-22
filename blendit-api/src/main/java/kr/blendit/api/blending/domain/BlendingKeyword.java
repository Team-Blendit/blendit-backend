package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name ="blending_keyword",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blending_keyword",
                        columnNames = {"blending_id", "keyword_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlendingKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "blending_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Blending blending;

    @JoinColumn(name = "keyword_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Keyword keyword;


    @Builder(access = AccessLevel.PRIVATE)
    private BlendingKeyword(Blending blending, Keyword keyword) {
        this.blending = blending;
        this.keyword = keyword;
    }

    public static BlendingKeyword create(Blending blending, Keyword keyword) {
        // Todo: Blending, Keyword null 체크

        return BlendingKeyword.builder()
                .blending(blending)
                .keyword(keyword)
                .build();
    }
}
