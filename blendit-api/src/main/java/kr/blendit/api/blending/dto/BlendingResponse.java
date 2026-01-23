package kr.blendit.api.blending.dto;

import kr.blendit.api.blending.constant.BlendingStatus;
import kr.blendit.api.blending.domain.Blending;
import kr.blendit.api.blending.domain.BlendingKeyword;
import kr.blendit.api.blending.domain.BlendingUser;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlendingResponse {

    private Long id;
    private String uuid;
    private List<UserTmp> participants;
    private String title;
    private String content;
    private List<String> keywords;
    private Integer capacity;
    private String region;
    private String place;
    private BlendingStatus status;
    private String openChattingUrl;
    private LocalDateTime schedule;
    private Long bookmarkCount;

    public static BlendingResponse from(Blending blending, long bookmarkCount) {
        return BlendingResponse.builder()
                .id(blending.getId())
                .uuid(blending.getUuid())
                .participants(extractParticipants(blending.getParticipants()))
                .title(blending.getTitle())
                .content(blending.getContent())
                .keywords(extractKeywords(blending.getKeywords()))
                .capacity(blending.getCapacity())
                .region(blending.getRegion())
                .place(blending.getPlace())
                .status(blending.getStatus())
                .openChattingUrl(blending.getOpenChattingUrl())
                .schedule(blending.getSchedule())
                .bookmarkCount(bookmarkCount)
                .build();
    }

    private static Integer extractBookmarkCount(Blending blending) {

    }

    private static List<String> extractKeywords(List<BlendingKeyword> blendingKeywords) {
        List<String> strKeywords = new ArrayList<>();

        for(BlendingKeyword blendingKeyword : blendingKeywords) {
            strKeywords.add(blendingKeyword.getKeyword().getName());
        }
        return strKeywords;
    }

    private static List<UserTmp> extractParticipants(List<BlendingUser> blendingUsers) {
        List<UserTmp> participants = new ArrayList<>();

        for(BlendingUser blendingUser : blendingUsers) {
            UserTmp userTmp = new UserTmp();
            userTmp.setUuid(blendingUser.getUser().getUuid());
            participants.add(userTmp);
        }
        return participants;
    }
}
