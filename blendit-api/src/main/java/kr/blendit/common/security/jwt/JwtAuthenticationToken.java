package kr.blendit.common.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwtToken;
    private final String uuid;

    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.setAuthenticated(false);
        this.jwtToken = jwtToken;
        this.uuid = null;
    }

    public JwtAuthenticationToken(String uuid, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        super.setAuthenticated(true);
        this.uuid = uuid;
        this.jwtToken = null;
    }

    @Override
    public String getCredentials() {
        return this.jwtToken;
    }

    @Override
    public String getPrincipal() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(jwtToken, that.jwtToken) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jwtToken, uuid);
    }
}
