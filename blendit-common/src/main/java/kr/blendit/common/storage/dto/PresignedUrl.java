package kr.blendit.common.storage.dto;

import java.time.Instant;

public record PresignedUrl(
    String key,
    String url,
    Instant expiresAt
) {

}
