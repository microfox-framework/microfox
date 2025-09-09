package ir.moke.microfox.http;

import ir.moke.kafir.utils.JsonUtils;
import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicrofoxException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class ResponseHelper {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);

    public static void body(String payload, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            writer.write(payload);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void body(Object o, HttpServletResponse response) {
        try {
            contentType(ContentType.APPLICATION_JSON, response);
            String json = JsonUtils.toJson(o);
            PrintWriter writer = response.getWriter();
            writer.write(json);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void sse(SseObject sseObject, HttpServletResponse response) {
        try {
            PrintWriter writer = response.getWriter();
            Optional.ofNullable(sseObject.retry()).ifPresent(item -> writer.write("retry: %s \n".formatted(sseObject.retry())));
            Optional.ofNullable(sseObject.id()).ifPresent(item -> writer.write("id: %s \n".formatted(sseObject.id())));
            Optional.ofNullable(sseObject.event()).ifPresent(item -> writer.write("event: %s \n".formatted(sseObject.event())));
            Optional.ofNullable(sseObject.data()).ifPresent(item -> writer.write("data: %s \n\n".formatted(sseObject.data())));
            writer.flush();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void contentType(ContentType contentType, HttpServletResponse response) {
        response.setContentType(contentType.getType());
    }

    public static void status(int status, HttpServletResponse response) {
        response.setStatus(status);
    }

    public static void contentLength(int length, HttpServletResponse response) {
        response.setContentLength(length);
    }

    public static void header(String name, String value, HttpServletResponse response) {
        response.addHeader(name, value);
    }

    public static void header(String name, int value, HttpServletResponse response) {
        response.addIntHeader(name, value);
    }

    public static void header(String name, Date value, HttpServletResponse response) {
        response.addDateHeader(name, value.getTime());
    }

    public static void header(String name, java.sql.Date value, HttpServletResponse response) {
        response.addDateHeader(name, value.getTime());
    }

    public static void header(String name, Instant value, HttpServletResponse response) {
        response.addDateHeader(name, value.toEpochMilli());
    }

    public static void cookie(Cookie cookie, HttpServletResponse response) {
        response.addCookie(cookie);
    }

    public static void redirect(String location, HttpServletResponse response) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void redirect(String location, int httpStatusCode, HttpServletResponse response) {
        response.setStatus(httpStatusCode);
        response.setHeader("Location", location);
        response.setHeader("Connection", "close");
        try {
            response.sendError(httpStatusCode);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void flushBuffer(HttpServletResponse response) {
        try {
            response.flushBuffer();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static ServletOutputStream outputStream(HttpServletResponse response) {
        try {
            return response.getOutputStream();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }
}
