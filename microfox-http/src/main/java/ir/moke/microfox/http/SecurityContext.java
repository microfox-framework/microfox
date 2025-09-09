package ir.moke.microfox.http;

import ir.moke.microfox.api.http.security.Credential;

public class SecurityContext {
    private static final ThreadLocal<Credential> CREDENTIAL_HOLDER = new ThreadLocal<>();

    public static void setCredential(Credential credential) {
        CREDENTIAL_HOLDER.set(credential);
    }

    public static Credential getCredential() {
        return CREDENTIAL_HOLDER.get();
    }

    public static void clear() {
        CREDENTIAL_HOLDER.remove();
    }
}
