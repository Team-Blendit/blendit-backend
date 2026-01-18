package kr.blendit.common.security.provider;

import kr.blendit.common.constant.UserRole;
import kr.blendit.common.security.jwt.JwtAuthenticationToken;
import kr.blendit.common.security.jwt.JwtProvider;
import kr.blendit.common.security.jwt.UserToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        String jwtToken = jwtAuth.getCredentials();
        UserToken userToken = jwtProvider.parseToken(jwtToken);

        List<SimpleGrantedAuthority> authorities = userToken.getRoles().stream()
                .map(UserRole::getValue)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new JwtAuthenticationToken(userToken.getUserUuid(), authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
