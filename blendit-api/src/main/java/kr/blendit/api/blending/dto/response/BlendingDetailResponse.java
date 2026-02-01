package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.constant.JoinStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import kr.blendit.api.blending.domain.BlendingUser;
import kr.blendit.api.common.constant.Position;
import lombok.*;
import org.hibernate.mapping.Join;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlendingDetailResponse {

    private Long id;
    private String uuid;
    private List<BlendingParticipantResponse> blendingParticipant;
    private String title;
    private String content;
    private Position position;
    private List<String> keywords;
    private Integer capacity;
    private String region;
    private BlendingStatus status;
    private String openChattingUrl;
    private LocalDateTime schedule;
    private Boolean autoApproval;
    private Long bookmarkCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Boolean isBookmarked;
    private Boolean isHost;
    private String currentUserJoinStatus;


    public static BlendingDetailResponse from(
            Blending blending, List<BlendingUser> blendingUsers, long bookmarkCount,
            boolean isBookmarked, boolean isHost, String currentUserJoinStatus) {

        return BlendingDetailResponse.builder()
                .id(blending.getId())
                .uuid(blending.getUuid())
                .blendingParticipant(BlendingParticipantResponse.from(blendingUsers))
                .title(blending.getTitle())
                .content(blending.getContent())
                .position(blending.getPosition())
                .keywords(BlendingKeyword.extractKeywords(blending.getKeywords()))
                .capacity(blending.getCapacity())
                .region(blending.getRegion())
                .status(blending.getStatus())
                .openChattingUrl(blending.getOpenChattingUrl())
                .schedule(blending.getSchedule())
                .bookmarkCount(bookmarkCount)
                .autoApproval(blending.getAutoApproval())
                .createdDate(blending.getCreatedDate())
                .lastModifiedDate(blending.getLastModifiedDate())
                .isBookmarked(isBookmarked)
                .isHost(isHost)
                .currentUserJoinStatus(currentUserJoinStatus)
                .build();
    }

}
