package kr.blendit.api.blending.domain;

import jakarta.persistence.*;
import kr.blendit.api.blending.constant.BlendingGrade;
import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.common.constant.Position;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Position position;

    @OneToMany(mappedBy = "blending", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<BlendingKeyword> keywords = new ArrayList<>();

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BlendingStatus status;

    @Column
    private String openChattingUrl;

    @Column(nullable = false)
    private LocalDateTime schedule;

    @Column(nullable = false)
    private Boolean autoApproval;


    /**
     * 블렌딩 생성자
     */
    @Builder(access = AccessLevel.PRIVATE)
    public Blending(String title, String content, Position position, int capacity, String region, String openChattingUrl, LocalDateTime schedule, Boolean autoApproval) {
        this.title = title;
        this.content = content;
        this.position = position;
        this.capacity = capacity;
        this.region = region;
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
    public static Blending create(String title, String content, Position position, Integer capacity, String region, String openChattingUrl, LocalDateTime schedule, Boolean autoApproval) {

        if(capacity < 2) {
            throw new BaseException(BaseErrorCode.BLENDING_CAPACITY_BELOW_MIN);
        }
        if(schedule.isBefore(LocalDateTime.now())) {
            throw new BaseException(BaseErrorCode.BLENDING_INVALID_SCHEDULE_TIME);
        }

        return Blending.builder()
                .title(title)
                .content(content)
                .position(position)
                .capacity(capacity)
                .region(region)
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
     * 참여자 제거
     */
    public void deleteParticipant(BlendingUser blendingUser) {
        this.getParticipants().remove(blendingUser);
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
    public void updateKeyword(List<Keyword> newKeywords) {
        // 기존 리스트에서 새 리스트에 없는 것 삭제
        this.keywords.removeIf(existing ->
                !newKeywords.contains(existing.getKeyword())
        );

        // 새 리스트에서 기존 리스트에 없는 것만 추가
        for (Keyword newKeyword : newKeywords) {
            boolean exists = this.keywords.stream()
                    .anyMatch(existing -> existing.getKeyword().equals(newKeyword));

            if (!exists) {
                addKeyword(newKeyword);
            }
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
    public void update(String title, String content, Position position, Integer capacity, String region, String openChattingUrl, LocalDateTime schedule, Boolean autoApproval) {
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        if(position != null) this.position = position;
        if(capacity != null) this.capacity = capacity;
        if(region != null) this.region = region;
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
     * 참여 승인 및 Host 상태의 블렌딩 유저 수 조회
     *
     * @apiNote Blending과 BlendingUser 까지 프록시 객체가 아닌 실제 엔티티를 들고 있는 경우 사용하기 적합합니다.
     */
    public int getCurrentParticipantCount() {
        int participantCount = 0;

        for(BlendingUser blendingUser : this.getParticipants()) {
            JoinStatus joinStatus = blendingUser.getJoinStatus();

            if(joinStatus.equals(JoinStatus.HOST) || joinStatus.equals(JoinStatus.APPROVED)) participantCount++;
        }

        return participantCount;
    }


    /**
     * 이 블렌딩에서 해당 유저가 HOST 등급인지 확인
     *
     * @apiNote Blending과 BlendingUser 까지 프록시 객체가 아닌 실제 엔티티를 들고 있는 경우 사용하기 적합합니다.
     */
    public boolean isHost(String userUuid) {
        boolean isHost = false;
        for(BlendingUser blendingUser : this.getParticipants()) {
            if(blendingUser.getBlendingGrade() == BlendingGrade.HOST) {
                if(blendingUser.getUser().getUuid().equals(userUuid)) isHost = true;
                break;
            }
        }
        return isHost;
    }


}



