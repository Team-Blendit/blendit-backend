package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.constant.Keyword;
import lombok.*;

@Entity
@Table(name ="blending_keyword")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlendingKeyword {

    @Id
    @Column(name = "blending_keyword_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "blending_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Blending blending;

    @Column(name = "keyword", nullable = false)
    @Enumerated(EnumType.STRING)
    private Keyword keyword;
}
