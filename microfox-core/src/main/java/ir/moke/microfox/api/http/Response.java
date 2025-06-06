package ir.moke.microfox.api.http;

import jakarta.servlet.http.Cookie;

import java.time.Instant;
import java.util.Date;

public interface Response {
    void body(String payload);

    void body(Object o);

    void contentType(ContentType contentType);

    void status(int status);

    void contentLength(int length);

    void header(String name, String value);

    void header(String header, int value);

    void header(String header, Date value);

    void header(String header, java.sql.Date value);

    void header(String header, Instant value);

    void cookie(Cookie cookie);

    void redirect(String location);

    void redirect(String location, int httpStatusCode);
}
