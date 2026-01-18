package kr.blendit.common.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import kr.blendit.common.exception.BaseErrorCode;
import kr.blendit.common.exception.BaseException;
import org.springframework.http.server.PathContainer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

public class SkipPathRequestMatcher implements RequestMatcher {

    private final List<PathPattern> skipPatterns;
    private final List<PathPattern> processingPatterns;
    private final PathPatternParser parser = new PathPatternParser();

    public SkipPathRequestMatcher(List<String> pathsToSkip, List<String> processingPaths) {
        if (pathsToSkip == null || processingPaths == null) {
            throw new BaseException(BaseErrorCode.INVALID_SECURITY_CONFIG);
        }
        this.skipPatterns = pathsToSkip.stream()
                .map(parser::parse)
                .toList();
        this.processingPatterns = processingPaths.stream()
                .map(parser::parse)
                .toList();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String path = request.getRequestURI();
        PathContainer pathContainer = PathContainer.parsePath(path);

        boolean shouldSkip = skipPatterns.stream()
                .anyMatch(pattern -> pattern.matches(pathContainer));
        boolean shouldProcess = processingPatterns.stream()
                .anyMatch(pattern -> pattern.matches(pathContainer));

        return !shouldSkip && shouldProcess;
    }
}
