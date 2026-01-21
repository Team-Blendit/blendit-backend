package kr.blendit.common.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(
    List<String> publicPaths
) {
    public SecurityProperties {
        if (publicPaths == null) {
            publicPaths = List.of();
        }
    }
}
