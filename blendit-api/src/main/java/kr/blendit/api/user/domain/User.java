package kr.blendit.api.user.domain;

import jakarta.persistence.*;
import kr.blendit.common.constant.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private Integer tokenVersion;

    @Builder
    public User(String uuid, String email, UserRole role) {
        this.uuid = uuid;
        this.email = email;
        this.role = role;
        this.tokenVersion = 0;
    }

    public void incrementTokenVersion() {
        this.tokenVersion++;
    }
}
