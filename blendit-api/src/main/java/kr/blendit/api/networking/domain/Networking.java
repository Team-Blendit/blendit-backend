package kr.blendit.api.networking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "networking")
@Builder
@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Networking {

    @Id
    @Column(name = "networking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // todo: private User host;

    /*
    todo:
        ManyToMany 관계이므로 중간 테이블 설계 필요
        private List<User> participants;
     */

    @Column(columnDefinition = "TEXT", nullable = false)
    @Lob
    private String content;

    @Column
    private int capacity;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private List<Position> position;

    @Column
    @Enumerated(EnumType.STRING)
    private List<Keyword> keywords;

    /*
    todo:
        ManyToMany 관계이므로 중간테이블 설계 필요
        private List<User> bookmark = 0;
     */

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.RECRUITING;

    @Column
    private String openChattingUrl;

    @Column(nullable = false)
    private LocalDateTime schedule;

    // todo: @MappedSuperclass로 createdAt, updatedAt 추가 필요
}
