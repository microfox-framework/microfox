package ir.moke.microfox.api.http;

import java.util.List;

public interface SecurityStrategy {
    Credential authenticate(Request request);
    boolean authorize(Credential credential, List<String> requiredAuthorities);
    boolean isRequired();  // some routes may not need auth
}
