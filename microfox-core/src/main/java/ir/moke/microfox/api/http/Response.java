package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.sse.SseObject;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;

import java.io.OutputStream;
import java.time.Instant;
import java.util.Date;

public interface Response {
    void body(String payload);

    void body(Object o);

    void sse(SseObject sseObject);

    void contentType(ContentType contentType);

    void status(int status);
    void status(StatusCode code);

    void contentLength(int length);

    void header(String name, String value);

    void header(String header, int value);

    void header(String header, Date value);

    void header(String header, java.sql.Date value);

    void header(String header, Instant value);

    void cookie(Cookie cookie);

    void redirect(String location);

    void redirect(String location, int httpStatusCode);

    void flushBuffer();

    ServletOutputStream outputStream() ;
}
