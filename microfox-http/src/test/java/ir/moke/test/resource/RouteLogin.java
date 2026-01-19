package ir.moke.test.resource;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.security.UsernamePasswordCredential;
import ir.moke.microfox.http.SecurityContext;
import ir.moke.test.security.TokenProvider;

import java.util.List;

public class RouteLogin implements Route {

    @Override
    public void handle(Request request, Response response) {
        UsernamePasswordCredential credential = (UsernamePasswordCredential) SecurityContext.getCredential();
        String token = TokenProvider.create(credential.username(), List.of("ADMIN"), List.of("read:users"));
        response.header("Authorization", "Bearer " + token);
    }
}
