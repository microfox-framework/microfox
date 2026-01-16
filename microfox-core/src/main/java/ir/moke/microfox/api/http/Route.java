package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.util.List;

@FunctionalInterface
public interface Route {

    void handle(Request request, Response response);

    default SecurityStrategy securityStrategy() {
        return null;
    }

    default List<String> roles() {
        return List.of();
    }

    default List<String> scopes() {
        return List.of();
    }
}
