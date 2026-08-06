package ir.moke.test.security;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

public class JwtPrincipal implements Principal {
    private final String name;
    private final List<String> roles;
    private final List<String> scopes;
    private final ZonedDateTime loginAt;
    private final ZonedDateTime expireAt;

    public JwtPrincipal(String name, List<String> roles, List<String> scopes, ZonedDateTime loginAt, ZonedDateTime expireAt) {
        this.name = name;
        this.roles = roles;
        this.scopes = scopes;
        this.loginAt = loginAt;
        this.expireAt = expireAt;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public ZonedDateTime getExpireAt() {
        return expireAt;
    }
}
