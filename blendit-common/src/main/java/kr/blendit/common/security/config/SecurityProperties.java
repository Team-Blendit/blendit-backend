package kr.blendit.common.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(
    List<PublicEndpoint> publicEndpoints,
    List<ProtectedEndpoint> protectedEndpoints
) {
    public SecurityProperties {
        if (publicEndpoints == null) {
            publicEndpoints = List.of();
        }
        if (protectedEndpoints == null) {
            protectedEndpoints = List.of();
        }
    }

    public record PublicEndpoint(
        String path,
        List<String> methods
    ) {
        public PublicEndpoint {
            if (methods == null || methods.isEmpty()) {
                methods = List.of("*");
            }
        }

        public boolean matchesMethod(String method) {
            return methods.contains("*") || methods.contains(method.toUpperCase());
        }
    }

    public record ProtectedEndpoint(
        String path,
        List<String> methods
    ) {
        public ProtectedEndpoint {
            if (methods == null || methods.isEmpty()) {
                methods = List.of("*");
            }
        }

        public boolean matchesMethod(String method) {
            return methods.contains("*") || methods.contains(method.toUpperCase());
        }
    }
}
