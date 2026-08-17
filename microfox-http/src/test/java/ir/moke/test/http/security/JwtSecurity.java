package ir.moke.test.http.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

public class JwtSecurity implements SecurityStrategy {
    @Override
    public Principal authenticate(Request request) {
        String token = request.header("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String tokenHash = token.substring("Bearer ".length());
            DecodedJWT verify = TokenProvider.verify(tokenHash);
            if (verify == null) return null;
            return new JwtPrincipal("john",
                    List.of("ADMIN"),
                    List.of("read:users"),
                    ZonedDateTime.now(),
                    ZonedDateTime.now().plusHours(1));
        }
        return null;
    }

    @Override
    public boolean authorize(Principal credential, List<String> roles, List<String> scopes) {
        if (credential instanceof JwtPrincipal jwt) {
            boolean hasRole = roles.isEmpty() || jwt.getRoles().stream().anyMatch(roles::contains);
            boolean hasScope = scopes.isEmpty() || jwt.getScopes().stream().anyMatch(scopes::contains);
            return hasRole && hasScope;
        }
        return false;
    }
}

