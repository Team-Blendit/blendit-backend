package kr.blendit.api.blending.dto.response;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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


    public static BlendingDetailResponse from(Blending blending, long bookmarkCount, boolean isBookmarked, boolean isHost) {

        return BlendingDetailResponse.builder()
                .id(blending.getId())
                .uuid(blending.getUuid())
                .blendingParticipant(BlendingParticipantResponse.from(blending.getParticipants(), isHost))
                .title(blending.getTitle())
                .content(blending.getContent())
                .keywords(extractKeywords(blending.getKeywords()))
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
                .build();
    }

    private static List<String> extractKeywords(List<BlendingKeyword> blendingKeywords) {
        List<String> strKeywords = new ArrayList<>();

        for(BlendingKeyword blendingKeyword : blendingKeywords) {
            strKeywords.add(blendingKeyword.getKeyword().getName());
        }
        return strKeywords;
    }

}
