package ir.moke.microfox.api.http.security;

import java.util.List;

public record UsernamePasswordCredential(String username, String password, List<String> roles) implements Credential {
    public UsernamePasswordCredential(String password, String username) {
        this(username, password, List.of());
    }
}
