package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.common.domain.Keyword;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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


    /**
     * 블렌딩 키워드 생성자
     */
    @Builder(access = AccessLevel.PRIVATE)
    private BlendingKeyword(Blending blending, Keyword keyword) {
        this.blending = blending;
        this.keyword = keyword;
    }


    /**
     * 블렌딩 키워드 생성
     */
    public static BlendingKeyword create(Blending blending, Keyword keyword) {

        if (blending == null || keyword == null) {
            throw new BaseException(BaseErrorCode.INVALID_PARAMETER);
        }

        return BlendingKeyword.builder()
                .blending(blending)
                .keyword(keyword)
                .build();
    }

    public static List<String> extractKeywords(List<BlendingKeyword> blendingKeywords) {
        List<String> strKeywords = new ArrayList<>();

        for(BlendingKeyword blendingKeyword : blendingKeywords) {
            strKeywords.add(blendingKeyword.getKeyword().getName());
        }
        return strKeywords;
    }
}
