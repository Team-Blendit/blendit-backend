package kr.blendit.api.blending.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlendingRequest {

    private String title;
    private String content;
    private List<String> keywords;
    private Integer capacity;
    private String region;
    private String place;
    private String openChattingUrl;
    private LocalDateTime schedule;
}
