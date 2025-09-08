package ir.moke.microfox.api.http;

public record UsernamePasswordCredential(String username, String password) implements Credential {

}
