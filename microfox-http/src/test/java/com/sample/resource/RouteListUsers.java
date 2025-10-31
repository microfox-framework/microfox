package com.sample.resource;

import com.sample.UserDTO;
import com.sample.security.JwtSecurity;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.SecuredRoute;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.util.List;

public class RouteListUsers implements SecuredRoute {
    @Override
    public SecurityStrategy securityStrategy() {
        return new JwtSecurity();
    }

    @Override
    public List<String> roles() {
        return List.of("ADMIN", "MEMBER");
    }

    @Override
    public List<String> scopes() {
        return List.of("read:users");
    }

    @Override
    public void handle(Request request, Response response) {
        UserDTO u1 = new UserDTO(1, "mahdi");
        UserDTO u2 = new UserDTO(2, "ali");
        UserDTO u3 = new UserDTO(3, "sina");
        List<UserDTO> users = List.of(u1, u2, u3);
        response.body(users);
    }
}
