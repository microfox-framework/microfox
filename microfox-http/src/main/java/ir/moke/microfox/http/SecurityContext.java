package ir.moke.microfox.http;

import ir.moke.microfox.api.http.security.Credential;

public class SecurityContext {
    private static final ScopedValue<Credential> SCOPED_VALUE = ScopedValue.newInstance();

    public static Credential getCredential() {
        return SCOPED_VALUE.get();
    }

    public static ScopedValue<Credential> getScopedValue() {
        return SCOPED_VALUE;
    }
}
