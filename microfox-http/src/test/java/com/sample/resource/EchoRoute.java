package com.sample.resource;

import com.sample.UserService;
import com.sample.exception.SampleException;
import com.sample.security.BasicAuthSecurity;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.SecuredRoute;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class EchoRoute implements SecuredRoute {
    @Override
    public SecurityStrategy securityStrategy() {
        return new BasicAuthSecurity();
    }

    @Override
    public List<String> authorities() {
        return List.of("REPLAY_SCOPE", "ECHO_SCOPE");
    }

    @Override
    public void handle(Request request, Response response) {
        String msg = UserService.checkUser();
        throw new SampleException("ERROR !!");
//        response.body(msg);
//        response.header("x-server-date".toUpperCase(), new Date());
//        response.header("x-server-date-instance".toUpperCase(), ZonedDateTime.now().toInstant());
    }
}
