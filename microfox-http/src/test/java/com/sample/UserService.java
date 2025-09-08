package com.sample;

import ir.moke.microfox.api.http.UsernamePasswordCredential;
import ir.moke.microfox.http.SecurityContext;

public class UserService {

    public static String checkUser() {
        UsernamePasswordCredential credential = (UsernamePasswordCredential) SecurityContext.getCredential();
        return "Executed by user: " + credential.username();
    }
}
