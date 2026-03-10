package kr.blendit.api.user.domain;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.blendit.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBookmark extends BaseEntity {

  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User user;

  @JoinColumn(name = "bookmarker_user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private User bookmarkUser;

  public static UserBookmark create(User user, User bookmarkUser) {
    return UserBookmark.builder()
        .user(user)
        .bookmarkUser(bookmarkUser)
        .build();
  }
}
