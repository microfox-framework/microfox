package com.sample.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TokenProvider {
    private static final Algorithm algorithm;
    private static final String issuer = "localhost";

    static {
        algorithm = Algorithm.HMAC512("random_secret_key");
    }

    public static String create(String username, List<String> roles, List<String> scopes) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("username", username)
                .withClaim("roles", roles)
                .withClaim("scopes", scopes)
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public static DecodedJWT verify(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }
}
