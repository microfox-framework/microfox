package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.sse.SseObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Instant;

public class ResponseProxy implements InvocationHandler {
    private final HttpServletResponse response;

    public ResponseProxy(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();

        if (name.equals("toString") && method.getParameterCount() == 0)
            return proxy.getClass().getName() + "@" + System.identityHashCode(proxy);
        if (name.equals("hashCode") && method.getParameterCount() == 0)
            return System.identityHashCode(proxy);
        if (name.equals("equals") && method.getParameterCount() == 1)
            return proxy == args[0];

        switch (name) {
            case "body" -> invokeBody(args);
            case "sse" -> ResponseHelper.sse((SseObject) args[0], response);
            case "contentType" -> ResponseHelper.contentType((ContentType) args[0], response);
            case "status" -> ResponseHelper.status(args[0], response);
            case "contentLength" -> ResponseHelper.contentLength((Integer) args[0], response);
            case "header" -> invokeHeader(args);
            case "cookie" -> ResponseHelper.cookie((Cookie) args[0], response);
            case "redirect" -> invokeRedirect(args);
            case "flushBuffer" -> ResponseHelper.flushBuffer(response);
            case "outputStream" -> {
                return ResponseHelper.outputStream(response);
            }
        }
        return null;
    }

    private void invokeRedirect(Object[] args) {
        if (args.length == 2) {
            ResponseHelper.redirect((String) args[0], (int) args[1], response);
        } else {
            ResponseHelper.redirect((String) args[0], response);
        }
    }

    private void invokeHeader(Object[] args) {
        String name = (String) args[0];
        Object value = args[1];
        if (value instanceof String str) {
            ResponseHelper.header(name, str, response);
        } else if (value instanceof Integer num) {
            ResponseHelper.header(name, num, response);
        } else if (value instanceof java.sql.Date date) {
            ResponseHelper.header(name, date, response);
        } else if (value instanceof java.util.Date date) {
            ResponseHelper.header(name, date, response);
        } else if (value instanceof Instant instant) {
            ResponseHelper.header(name, instant, response);
        }
    }

    private void invokeBody(Object[] args) {
        if (args[0] instanceof String) {
            ResponseHelper.body((String) args[0], response);
        } else {
            ResponseHelper.body(args[0], response);
        }
    }
}
