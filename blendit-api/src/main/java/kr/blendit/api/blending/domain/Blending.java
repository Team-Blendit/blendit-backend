package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.common.domain.Keyword;
import kr.blendit.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blending")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blending extends BaseEntity {

    @OneToMany(mappedBy = "blending", orphanRemoval = true)
    private List<BlendingUser> participants = new ArrayList<>();

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "blending", orphanRemoval = true)
    private List<BlendingKeyword> keywords = new ArrayList<>();

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BlendingStatus status;

    @Column
    private String openChattingUrl;

    @Column
    private LocalDateTime schedule;

    @Builder(access = AccessLevel.PRIVATE)
    public Blending(String title, String content, int capacity, String region, String place, String openChattingUrl, LocalDateTime schedule) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.region = region;
        this.place = place;
        this.status = BlendingStatus.RECRUITING;
        this.openChattingUrl = openChattingUrl;
        this.schedule = schedule;
    }

    public static Blending create(String title, String content, int capacity, String region, String place, String openChattingUrl, LocalDateTime schedule) {
        // TODO: capacity, schedule 유효성 검사
        return Blending.builder()
                .title(title)
                .content(content)
                .capacity(capacity)
                .region(region)
                .place(place)
                .openChattingUrl(openChattingUrl)
                .schedule(schedule)
                .build();
    }

    public void addKeyword(Keyword keyword) {
        BlendingKeyword blendingKeyword = BlendingKeyword.create(this, keyword);

        this.keywords.add(blendingKeyword);
    }

}



