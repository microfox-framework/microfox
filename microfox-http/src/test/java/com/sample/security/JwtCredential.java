package com.sample.security;

import ir.moke.microfox.api.http.Credential;

import java.time.ZonedDateTime;
import java.util.List;

public record JwtCredential(String username, List<String> authorities, ZonedDateTime loginAt, ZonedDateTime expireAt) implements Credential {
    // or any other informations needed
}
