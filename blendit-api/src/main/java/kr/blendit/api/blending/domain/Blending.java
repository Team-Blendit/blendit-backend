package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.user.domain.User;
import kr.blendit.api.common.domain.Keyword;
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

    @OneToMany(mappedBy = "blending", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<BlendingKeyword> keywords = new ArrayList<>();

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String place; // Todo: 구체적인 장소는 제외된 것인지 확인 필요

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BlendingStatus status;

    @Column
    private String openChattingUrl;

    @Column(nullable = false)
    private LocalDateTime schedule;

    @Column(nullable = false)
    private Boolean autoApproval;

    // Todo: 직군 필드 필요 (User와 함께 사용해야한다. keyword 처럼 테이블로 분리하고 중간 테이블 연결 필요)

    /**
     * 블렌딩 생성자
     */
    @Builder(access = AccessLevel.PRIVATE)
    public Blending(String title, String content, int capacity, String region, String place, String openChattingUrl, LocalDateTime schedule, Boolean autoApproval) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.region = region;
        this.place = place;
        this.status = BlendingStatus.RECRUITING;
        this.openChattingUrl = openChattingUrl;
        this.schedule = schedule;
        this.autoApproval = autoApproval;
    }


    /**
     * 블렌딩 생성
     *
     * @apiNote 최소 인원 2명 / 일정은 과거 불가
     */
    public static Blending create(String title, String content, Integer capacity, String region, String place, String openChattingUrl, LocalDateTime schedule, Boolean autoApproval) {

        if(capacity < 2) {
            throw new BaseException(BaseErrorCode.BLENDING_CAPACITY_BELOW_MIN);
        }
        if(schedule.isBefore(LocalDateTime.now())) {
            throw new BaseException(BaseErrorCode.BLENDING_INVALID_SCHEDULE_TIME);
        }

        return Blending.builder()
                .title(title)
                .content(content)
                .capacity(capacity)
                .region(region)
                .place(place)
                .openChattingUrl(openChattingUrl)
                .schedule(schedule)
                .autoApproval(autoApproval)
                .build();
    }


    /**
     * 참여자 추가
     *
     * @param blendingGrade 참여자 권한
     * @param joinStatus 참여 상태(승인, 거절 등)
     */
    public BlendingUser addParticipant(User user, BlendingGrade blendingGrade, String message, JoinStatus joinStatus) {
        BlendingUser blendingUser = BlendingUser.create(user, this, blendingGrade, message, joinStatus);
        this.participants.add(blendingUser);
        return blendingUser;
    }


    /**
     * 키워드 추가
     */
    public void addKeyword(Keyword keyword) {
        BlendingKeyword blendingKeyword = BlendingKeyword.create(this, keyword);

        this.keywords.add(blendingKeyword);
    }


    /**
     * 키워드 변경
     *
     * @apiNote addKeyword() 호출
     */
    public void updateKeyword(List<Keyword> keywords) {
        this.keywords.clear();

        for(Keyword keyword : keywords) {
            addKeyword(keyword);
        }
    }


    /**
     * 블렌딩 논리 삭제
     */
    public void delete() {
        this.setUseFlag(false);
    }


    /**
     * 블렌딩 정보 수정
     */
    public void update(String title, String content, Integer capacity, String region, String place, String openChattingUrl, LocalDateTime schedule, Boolean autoApproval) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(capacity != null) this.capacity = capacity;
        if(region != null) this.region = region;
        if(place != null) this.place = place;
        this.openChattingUrl = openChattingUrl;
        if(schedule != null) this.schedule = schedule;
        if(autoApproval != null) this.autoApproval = autoApproval;
    }


    /**
     * 블렌딩 상태 변경
     *
     * @param blendingStatus 변경하려는 상태
     * @apiNote 인원이 가득찬 경우엔 모집 중으로 변경 불가
     */
    public void updateStatus(BlendingStatus blendingStatus) {

        if(blendingStatus == BlendingStatus.RECRUITING) {
            int participantCount = this.getCurrentParticipantCount();

            // 호스트 + 승인된 사람이 정원 이상이라면 예외 발생
            if(participantCount >= this.capacity) {
                throw new BaseException(BaseErrorCode.BLENDING_CANNOT_RECRUIT_FULL);
            }
        }

        this.status = blendingStatus;
    }


    /**
     * 호스트 + 승인된 유저 수 추출 메서드
     */
    public int getCurrentParticipantCount() {
        int participantCount = 0;

        for(BlendingUser blendingUser : this.getParticipants()) {
            JoinStatus joinStatus = blendingUser.getJoinStatus();

            if(joinStatus.equals(JoinStatus.HOST) || joinStatus.equals(JoinStatus.APPROVED)) participantCount++;
        }

        return participantCount;
    }


}



