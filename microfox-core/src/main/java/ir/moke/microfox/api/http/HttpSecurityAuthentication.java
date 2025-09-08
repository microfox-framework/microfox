package ir.moke.microfox.api.http;

public interface HttpSecurityAuthentication {
    boolean isValid(Credential credential);
}
