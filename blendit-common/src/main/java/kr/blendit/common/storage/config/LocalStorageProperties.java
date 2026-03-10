package kr.blendit.common.storage.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.local")
public record LocalStorageProperties(
    String rootDir,
    String publicBaseUrl,
    long maxUploadBytes,
    List<String> allowedMimeTypes
) {

}
