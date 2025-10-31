package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.util.List;

public interface SecuredRoute extends Route {
    SecurityStrategy securityStrategy();

    List<String> roles();

    List<String> scopes();
}
