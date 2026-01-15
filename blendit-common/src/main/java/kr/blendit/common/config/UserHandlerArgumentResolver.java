package kr.blendit.common.config;

import kr.blendit.common.constant.UserRole;
import kr.blendit.common.security.exception.JwtTokenErrorCode;
import kr.blendit.common.security.exception.JwtTokenException;
import kr.blendit.common.security.jwt.CurrentUser;
import kr.blendit.common.security.jwt.JwtAuthenticationToken;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Component
public class UserHandlerArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CurrentUser.class);
    }

    @Override
    public CurrentUser resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                       NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new JwtTokenException(JwtTokenErrorCode.TOKEN_NOT_FOUND);
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new JwtTokenException(JwtTokenErrorCode.INVALID_TOKEN);
        }

        String userUuid = jwtAuth.getPrincipal();
        List<UserRole> roles = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::of)
                .toList();

        return new CurrentUser(userUuid, roles);
    }
}
