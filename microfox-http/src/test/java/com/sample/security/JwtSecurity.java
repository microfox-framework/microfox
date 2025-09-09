package com.sample.security;

import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.time.ZonedDateTime;
import java.util.List;

public class JwtSecurity implements SecurityStrategy {
    @Override
    public Credential authenticate(Request request) {
        String token = request.header("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return new JwtCredential("john",
                    List.of("ECHO_SCOPE"),
                    ZonedDateTime.now(),
                    ZonedDateTime.now().plusHours(1));
        }
        return null;
    }

    @Override
    public boolean authorize(Credential credential, List<String> authorities) {
        if (credential instanceof JwtCredential jwt) {
            return jwt.authorities().containsAll(authorities);
        }
        return false;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}

