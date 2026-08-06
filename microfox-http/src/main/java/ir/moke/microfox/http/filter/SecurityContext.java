package ir.moke.microfox.http.filter;

import java.security.Principal;

public class SecurityContext {
    private static final ScopedValue<Principal> SCOPED_VALUE = ScopedValue.newInstance();

    public static Principal principal() {
        return SCOPED_VALUE.get();
    }

    static ScopedValue<Principal> getScopedValue() {
        return SCOPED_VALUE;
    }
}
