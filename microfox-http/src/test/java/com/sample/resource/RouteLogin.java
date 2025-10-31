package com.sample.resource;

import com.sample.security.BasicAuthSecurity;
import com.sample.security.TokenProvider;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.SecuredRoute;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.security.UsernamePasswordCredential;
import ir.moke.microfox.http.SecurityContext;

import java.util.List;

public class RouteLogin implements SecuredRoute {
    @Override
    public SecurityStrategy securityStrategy() {
        return new BasicAuthSecurity();
    }

    @Override
    public List<String> roles() {
        return null;
    }

    @Override
    public List<String> scopes() {
        return null;
    }

    @Override
    public void handle(Request request, Response response) {
        UsernamePasswordCredential credential = (UsernamePasswordCredential) SecurityContext.getCredential();
        String token = TokenProvider.create(credential.username(), List.of("ADMIN"), List.of("read:users"));
        response.header("Authorization", "Bearer " + token);
    }
}
