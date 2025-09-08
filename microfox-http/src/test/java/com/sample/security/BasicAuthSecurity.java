package com.sample.security;

import ir.moke.microfox.api.http.Credential;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.SecurityStrategy;
import ir.moke.microfox.api.http.UsernamePasswordCredential;

import java.util.List;

public class BasicAuthSecurity implements SecurityStrategy {

    @Override
    public Credential authenticate(Request request) {
        /*
        *
        * String auth = request.header("Authorization");
        * if (auth != null && auth.startsWith("Basic ")) {
        *     // decode user:pass
        *     return new UsernamePasswordCredential("user", "pass");
        * }
        * */
        return new UsernamePasswordCredential("admin", "adminpass");
    }

    @Override
    public boolean authorize(Credential credential, List<String> authorities) {
        return true; // BasicAuth here only authenticates, no roles
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
