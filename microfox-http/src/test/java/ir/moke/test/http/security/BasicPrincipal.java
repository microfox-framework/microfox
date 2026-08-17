package ir.moke.test.http.security;

import java.security.Principal;

public class BasicPrincipal implements Principal {
    private final String name;

    public BasicPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
