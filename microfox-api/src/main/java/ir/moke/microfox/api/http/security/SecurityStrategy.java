package ir.moke.microfox.api.http.security;

import ir.moke.microfox.api.http.Request;

import java.security.Principal;
import java.util.List;

public interface SecurityStrategy {
    Principal authenticate(Request request);

    boolean authorize(Principal principal, List<String> roles, List<String> scopes);
}
