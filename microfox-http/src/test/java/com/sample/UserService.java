package com.sample;

import ir.moke.microfox.api.http.security.UsernamePasswordCredential;
import ir.moke.microfox.http.SecurityContext;

public class UserService {

    public static String checkUser() {
        UsernamePasswordCredential credential = (UsernamePasswordCredential) SecurityContext.getCredential();
        if (credential != null) {
            return "Executed by user: " + credential.username();
        }

        return null;
    }
}
