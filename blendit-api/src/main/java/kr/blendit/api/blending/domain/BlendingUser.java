package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.BlendingUserGrade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.user.domain.User;
import kr.blendit.common.entity.BaseTimeEntity;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import lombok.*;

@Entity
@Table(
        name = "blending_user",
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

  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private User user;

  @JoinColumn(name = "blending_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Blending blending;

  @Column(name = "blending_user_grade", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private BlendingUserGrade blendingUserGrade;

  @Column(name = "message")
  private String message;

  @Column(name = "join_status", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private JoinStatus joinStatus;


  /**
   * 블렌딩 유저 생성자
   */
  @Builder(access = AccessLevel.PRIVATE)
  private BlendingUser(User user, Blending blending, BlendingUserGrade blendingUserGrade,
                       String message, JoinStatus joinStatus) {
    this.user = user;
    this.blending = blending;
    this.blendingUserGrade = blendingUserGrade;
    this.message = message;
    this.joinStatus = joinStatus;
  }


  /**
   * 블렌딩 유저 생성
   */
  public static BlendingUser create(User user, Blending blending, BlendingUserGrade blendingUserGrade,
                                    String message, JoinStatus joinStatus) {

    if (user == null || blending == null || blendingUserGrade == null || joinStatus == null) {
      throw new BaseException(BaseErrorCode.INVALID_PARAMETER);
    }

    return BlendingUser.builder()
            .user(user)
            .blending(blending)
            .blendingUserGrade(blendingUserGrade)
            .message(message)
            .joinStatus(joinStatus)
            .build();
  }

  public void updateJoinStatus(JoinStatus joinStatus) {
    this.joinStatus = joinStatus;
  }

  public void updateMessage(String message) {
    this.message = message;
  }

  public void cancelParticipant() {
    this.joinStatus = JoinStatus.CANCEL;
  }
}
