package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.http.HttpUtils;
import ir.moke.utils.JsonUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class ResponseHelper {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHelper.class);

    public static void body(String payload, HttpServletResponse response) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(payload.getBytes());
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void body(Object o, HttpServletResponse response) {
        try {
            contentType(ContentType.APPLICATION_JSON, response);
            String json = JsonUtils.toJson(o);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(json.getBytes());
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void sse(SseObject sseObject, HttpServletResponse response) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            Optional.ofNullable(sseObject.retry()).ifPresent(item -> HttpUtils.sendResponse(response, "retry: %s \n".formatted(sseObject.retry()).getBytes()));
            Optional.ofNullable(sseObject.id()).ifPresent(item -> HttpUtils.sendResponse(response, "id: %s \n".formatted(sseObject.id()).getBytes()));
            Optional.ofNullable(sseObject.event()).ifPresent(item -> HttpUtils.sendResponse(response, "event: %s \n".formatted(sseObject.event()).getBytes()));
            Optional.ofNullable(sseObject.data()).ifPresent(item -> HttpUtils.sendResponse(response, "data: %s \n\n".formatted(sseObject.data()).getBytes()));
            outputStream.flush();
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    public static void contentType(ContentType contentType, HttpServletResponse response) {
        response.setContentType(contentType.getType());
    }

    public static void status(Object o, HttpServletResponse response) {
        if (o instanceof StatusCode code) {
            response.setStatus(code.getCode());
        } else {
            response.setStatus((Integer) o);
        }
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
        try {
            response.sendRedirect(location, httpStatusCode);
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
