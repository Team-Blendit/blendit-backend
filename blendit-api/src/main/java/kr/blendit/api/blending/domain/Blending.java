package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.common.entity.BaseTimeEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blending")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blending extends BaseTimeEntity {

    @Id
    @Column(name = "blending_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "blending", orphanRemoval = true)
    @Builder.Default
    private List<BlendingUser> participants = new ArrayList<>();

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private int capacity;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BlendingStatus status = BlendingStatus.RECRUITING;

    @Column
    private String openChattingUrl;

    @Column(nullable = false)
    private LocalDateTime schedule;

    @Column
    @Builder.Default
    private boolean useFlag = true;
}
