package kr.blendit.common.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import kr.blendit.common.security.config.SecurityProperties.PublicEndpoint;
import org.springframework.http.server.PathContainer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkipPathRequestMatcher implements RequestMatcher {

    private final Map<PathPattern, PublicEndpoint> publicEndpointMap;
    private final List<PathPattern> processingPatterns;
    private final PathPatternParser parser = new PathPatternParser();

    public SkipPathRequestMatcher(List<PublicEndpoint> publicEndpoints, List<String> processingPaths) {
        if (publicEndpoints == null || processingPaths == null) {
            throw new BaseException(BaseErrorCode.INVALID_SECURITY_CONFIG);
        }
        this.publicEndpointMap = publicEndpoints.stream()
                .collect(Collectors.toMap(
                        endpoint -> parser.parse(endpoint.path()),
                        endpoint -> endpoint
                ));
        this.processingPatterns = processingPaths.stream()
                .map(parser::parse)
                .toList();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        PathContainer pathContainer = PathContainer.parsePath(path);

        boolean shouldSkip = publicEndpointMap.entrySet().stream()
                .anyMatch(entry ->
                        entry.getKey().matches(pathContainer) &&
                        entry.getValue().matchesMethod(method)
                );

        boolean shouldProcess = processingPatterns.stream()
                .anyMatch(pattern -> pattern.matches(pathContainer));

        return !shouldSkip && shouldProcess;
    }

    /**
     * 요청이 public 엔드포인트인지 확인 (Optional auth용)
     */
    public boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        PathContainer pathContainer = PathContainer.parsePath(path);

        return publicEndpointMap.entrySet().stream()
                .anyMatch(entry ->
                        entry.getKey().matches(pathContainer) &&
                        entry.getValue().matchesMethod(method)
                );
    }
}
