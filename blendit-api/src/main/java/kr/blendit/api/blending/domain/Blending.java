package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.Grade;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.user.domain.User;
import kr.blendit.common.entity.BaseEntity;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
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

    public static Blending create(String title, String content, Integer capacity, String region, String place, String openChattingUrl, LocalDateTime schedule) {

        if(capacity < 2) {
            throw new BaseException(BaseErrorCode.BLENDING_CAPACITY_BELOW_MIN);
        }

        if(schedule != null) {
            if(schedule.isBefore(LocalDateTime.now())) {
                throw new BaseException(BaseErrorCode.BLENDING_INVALID_SCHEDULE_TIME);
            }
        }


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

    public BlendingUser addParticipant(User user, Grade grade, String message, JoinStatus joinStatus) {
        BlendingUser blendingUser = BlendingUser.create(user, this, grade, message, joinStatus);
        this.participants.add(blendingUser);
        return blendingUser;
    }

    public void addKeyword(Keyword keyword) {
        BlendingKeyword blendingKeyword = BlendingKeyword.create(this, keyword);

        this.keywords.add(blendingKeyword);
    }

    public void updateKeyword(List<Keyword> keywords) {
        this.keywords.clear();

        for(Keyword keyword : keywords) {
            addKeyword(keyword);
        }
    }

    public void delete() {
        this.setUseFlag(false);
    }

    public void update(String title, String content, Integer capacity, String region, String place, String openChattingUrl, LocalDateTime schedule) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(capacity != null) this.capacity = capacity;
        if(region != null) this.region = region;
        if(place != null) this.place = place;
        this.openChattingUrl = openChattingUrl;
        this.schedule = schedule;
    }

    public void updateStatus(BlendingStatus blendingStatus) {

        if(blendingStatus == BlendingStatus.RECRUITING && this.participants.size() >= this.capacity) {
            throw new BaseException(BaseErrorCode.BLENDING_CANNOT_RECRUIT_FULL);
        }

        this.status = blendingStatus;
    }


}



