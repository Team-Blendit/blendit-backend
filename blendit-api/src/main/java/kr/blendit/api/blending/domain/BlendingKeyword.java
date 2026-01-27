package kr.blendit.api.blending.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.blendit.api.keyword.domain.Keyword;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "blending_keyword",
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
