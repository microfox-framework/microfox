package com.sample.security;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.security.UsernamePasswordCredential;

import java.util.Base64;
import java.util.List;

public class BasicAuthSecurity implements SecurityStrategy {

    @Override
    public Credential authenticate(Request request) {

        String auth = request.header("Authorization");
        if (auth != null && auth.startsWith("Basic ")) {
            byte[] bytes = Base64.getDecoder().decode(auth.substring(6, auth.length() - 1));
            String credential = new String(bytes);
            String username = credential.split(":")[0];
            String password = credential.split(":")[1];

            // check username & password on db
            return new UsernamePasswordCredential(username, password);
        }
        return null;
    }

    @Override
    public boolean authorize(Credential credential, List<String> roles, List<String> scopes) {
        return true; // BasicAuth here only authenticates, no roles
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
