package com.sample.resource;

import com.sample.exception.SampleException;
import com.sample.security.BasicAuthSecurity;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.util.List;

public class SecureRoute implements ir.moke.microfox.api.http.SecuredRoute {
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
        throw new SampleException("Secure ERROR !!");
    }
}
