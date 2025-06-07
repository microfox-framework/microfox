package ir.moke.microfox.http;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.utils.JsonUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class ResponseImpl implements Response {
    private static final Logger logger = LoggerFactory.getLogger(ResponseImpl.class);
    private final HttpServletResponse response;

    public ResponseImpl(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void body(String payload) {
        try {
            PrintWriter writer = response.getWriter();
            writer.write(payload);
            writer.flush();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void body(Object o) {
        try {
            contentType(ContentType.APPLICATION_JSON);
            String json = JsonUtils.toJson(o);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void sse(String event, String data, String id, Long retry) {
        try {
            PrintWriter writer = response.getWriter();
            Optional.ofNullable(retry).ifPresent(item -> writer.write("retry: %s \n".formatted(retry)));
            Optional.ofNullable(id).ifPresent(item -> writer.write("id: %s \n".formatted(id)));
            Optional.ofNullable(event).ifPresent(item -> writer.write("event: %s \n".formatted(event)));
            Optional.ofNullable(data).ifPresent(item -> writer.write("data: %s \n\n".formatted(data)));
            writer.flush();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void sse(String event, String data, String id) {
        sse(event, data, id, null);
    }

    @Override
    public void sse(String event, String data) {
        sse(event, data, null, null);
    }

    @Override
    public void sse(String data) {
        sse(data, null, null, null);
    }

    @Override
    public void contentType(ContentType contentType) {
        response.setContentType(contentType.getType());
    }

    @Override
    public void status(int status) {
        response.setStatus(status);
    }

    @Override
    public void contentLength(int length) {
        response.setContentLength(length);
    }

    @Override
    public void header(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public void header(String header, int value) {
        response.addIntHeader(header, value);
    }

    @Override
    public void header(String header, Date value) {
        response.addDateHeader(header, value.getTime());
    }

    @Override
    public void header(String header, java.sql.Date value) {
        response.addDateHeader(header, value.getTime());
    }

    @Override
    public void header(String header, Instant value) {
        response.addDateHeader(header, value.toEpochMilli());
    }

    @Override
    public void cookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    @Override
    public void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException ioException) {
            logger.warn("Redirect failure", ioException);
        }
    }

    @Override
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
