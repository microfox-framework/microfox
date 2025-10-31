package com.sample.security;

import ir.moke.microfox.api.http.security.Credential;

import java.time.ZonedDateTime;
import java.util.List;

public record JwtCredential(String username,
                            List<String> roles,
                            List<String> scopes,
                            ZonedDateTime loginAt,
                            ZonedDateTime expireAt) implements Credential {
    // or any other informations needed
}
