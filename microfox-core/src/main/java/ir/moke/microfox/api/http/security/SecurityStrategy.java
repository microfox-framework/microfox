package ir.moke.microfox.api.http.security;

import ir.moke.microfox.api.http.Request;

import java.util.List;

public interface SecurityStrategy {
    Credential authenticate(Request request);
    boolean authorize(Credential credential, List<String> requiredAuthorities);
    boolean isRequired();  // some routes may not need auth
}
