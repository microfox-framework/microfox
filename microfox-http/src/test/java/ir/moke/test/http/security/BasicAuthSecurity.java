package ir.moke.test.http.security;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.security.Principal;
import java.util.Base64;
import java.util.List;

public class BasicAuthSecurity implements SecurityStrategy {

    @Override
    public Principal authenticate(Request request) {

        String auth = request.header("Authorization");
        if (auth != null && auth.startsWith("Basic ")) {
            byte[] bytes = Base64.getDecoder().decode(auth.split("\\s+")[1]);
            String credential = new String(bytes);
            String username = credential.split(":")[0];
            String password = credential.split(":")[1];

            /*
             * check username & password on db
             * */

            // return principal
            return new BasicPrincipal(username);
        }
        return null;
    }

    @Override
    public boolean authorize(Principal credential, List<String> roles, List<String> scopes) {
        return true; // BasicAuth here only authenticates, no roles
    }
}
