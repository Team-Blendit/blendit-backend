package kr.blendit.api.user.domain;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.blendit.api.keyword.domain.Keyword;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_keyword",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_keyword",
            columnNames = {"user_id", "keyword_id"}
        )
    }
)
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserKeyword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User user;

  @JoinColumn(name = "keyword_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Keyword keyword;

  public static UserKeyword create(User user, Keyword keyword) {
    return UserKeyword.builder()
        .user(user)
        .keyword(keyword)
        .build();
  }
}
