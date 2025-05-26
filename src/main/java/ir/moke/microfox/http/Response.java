package ir.moke.microfox.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Date;

public class Response {
    private static final Logger logger = LoggerFactory.getLogger(Response.class);
    private final HttpServletResponse response;

    public Response(HttpServletResponse response) {
        this.response = response;
    }

    public void body(String payload) {
        try {
            PrintWriter writer = response.getWriter();
            writer.write(payload);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void body(Object o) {
        try {
            contentType(ContentType.APPLICATION_JSON);
            String json = JsonUtils.toJson(o);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void contentType(ContentType contentType) {
        response.setContentType(contentType.getType());
    }

    public void status(int status) {
        response.setStatus(status);
    }

    public void contentLength(int length) {
        response.setContentLength(length);
    }

    public void header(String name, String value) {
        response.setHeader(name, value);
    }

    public void header(String header, int value) {
        response.addIntHeader(header, value);
    }

    public void header(String header, Date value) {
        response.addDateHeader(header, value.getTime());
    }

    public void header(String header, java.sql.Date value) {
        response.addDateHeader(header, value.getTime());
    }

    public void header(String header, Instant value) {
        response.addDateHeader(header, value.toEpochMilli());
    }
    public void cookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException ioException) {
            logger.warn("Redirect failure", ioException);
        }
    }

    public void redirect(String location, int httpStatusCode) {
        response.setStatus(httpStatusCode);
        response.setHeader("Location", location);
        response.setHeader("Connection", "close");
        try {
            response.sendError(httpStatusCode);
        } catch (IOException e) {
            logger.warn("Exception when trying to redirect permanently", e);
        }
    }
}
