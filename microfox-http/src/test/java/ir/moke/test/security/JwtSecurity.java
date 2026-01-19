package ir.moke.test.security;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.time.ZonedDateTime;
import java.util.List;

public class JwtSecurity implements SecurityStrategy {
    @Override
    public Credential authenticate(Request request) {
        String token = request.header("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String tokenHash = token.substring("Bearer ".length());
            TokenProvider.verify(tokenHash);
            return new JwtCredential("john",
                    List.of("ADMIN"),
                    List.of("read:users"),
                    ZonedDateTime.now(),
                    ZonedDateTime.now().plusHours(1));
        }
        return null;
    }

    @Override
    public boolean authorize(Credential credential, List<String> roles, List<String> scopes) {
        if (credential instanceof JwtCredential jwt) {
            boolean hasRole = roles.isEmpty() || jwt.roles().stream().anyMatch(roles::contains);
            boolean hasScope = scopes.isEmpty() || jwt.scopes().stream().anyMatch(scopes::contains);
            return hasRole && hasScope;
        }
        return false;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}

