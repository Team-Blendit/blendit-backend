package kr.blendit.common.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ncp.object-storage")
public record ObjectStorageProperties(
    String endpoint,
    String region,
    String bucket,
    String accessKey,
    String secretKey,
    String defaultPrefix,
    String publicBaseUrl
) {

}
