package ir.moke.test.resource;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.http.SecurityContext;
import ir.moke.test.security.BasicPrincipal;
import ir.moke.test.security.TokenProvider;

import java.util.List;

public class RouteLogin implements Route {

    @Override
    public void handle(Request request, Response response) {
        BasicPrincipal principal = (BasicPrincipal) SecurityContext.principal();
        String token = TokenProvider.create(principal.getName(), List.of("ADMIN"), List.of("read:users"));
        response.header("Authorization", "Bearer " + token);
    }
}
