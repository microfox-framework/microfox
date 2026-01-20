package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.http.HttpUtils;
import ir.moke.microfox.http.validation.MicroFoxValidator;
import ir.moke.utils.JsonUtils;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class RequestHelper {
    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);

    public static Locale locale(HttpServletRequest request) {
        return request.getLocale();
    }

    public static String body(HttpServletRequest request) {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T body(Class<T> clazzType, HttpServletRequest request) {
        try (ServletInputStream inputStream = request.getInputStream()) {
            String json = new String(inputStream.readAllBytes()).trim();
            T object = JsonUtils.toObject(json, clazzType);
            MicroFoxValidator.validate(object);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] bodyAsBytes(HttpServletRequest request) {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String authType(HttpServletRequest request) {
        return request.getAuthType();
    }

    public static Set<String> headers(HttpServletRequest request) {
        Set<String> headers = new HashSet<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            headers.add(enumeration.nextElement());
        }
        return headers;
    }

    public static String header(String name, HttpServletRequest request) {
        return request.getHeader(name);
    }

    public static Set<String> queryParameters(HttpServletRequest request) {
        Set<String> params = new HashSet<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            params.add(enumeration.nextElement());
        }
        return params;
    }

    public static String queryParameter(String key, HttpServletRequest request) {
        return request.getParameter(key);
    }

    public static String pathParam(String key, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        RouteInfo optionalRouteInfo = HttpUtils.findMatchingRouteInfo(requestURI, Method.valueOf(method.toUpperCase())).get();

        Map<String, String> map = HttpUtils.extractPathParams(optionalRouteInfo.path(), requestURI);
        return map.get(key);

    }

    public static Map<String, String> cookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Map<String, String> map = new HashMap<>();
        for (Cookie cookie : cookies) {
            map.put(cookie.getName(), cookie.getValue());
        }

        return map;
    }

    public static String cookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String userAgent(HttpServletRequest request) {
        return request.getHeader("user-agent");
    }

    public static String user(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    public static String contentType(HttpServletRequest request) {
        return request.getContentType();
    }

    public static String remoteIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static String localIp(HttpServletRequest request) {
        return request.getLocalAddr();
    }

    public static String url(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    public static String contextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    public static String servletPath(HttpServletRequest request) {
        return request.getServletPath();
    }

    public static String pathInfo(HttpServletRequest request) {
        return request.getPathInfo();
    }

    public static int port(HttpServletRequest request) {
        return request.getServerPort();
    }

    public static String scheme(HttpServletRequest request) {
        return request.getScheme();
    }

    public static String host(HttpServletRequest request) {
        return request.getHeader("host");
    }

    public static int contentLength(HttpServletRequest request) {
        return request.getContentLength();
    }

    public static String uri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    public static String protocol(HttpServletRequest request) {
        return request.getProtocol();
    }

    public static void setAttribute(String attribute, Object value, HttpServletRequest request) {
        request.setAttribute(attribute, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(String attribute, HttpServletRequest request) {
        return (T) request.getAttribute(attribute);
    }

    public static Set<String> attributes(HttpServletRequest request) {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = request.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    public static AsyncContext asyncContext(HttpServletRequest request) {
        return request.getAsyncContext();
    }

    public static AsyncContext startAsync(HttpServletRequest request) {
        return request.startAsync();
    }

    public static boolean isAsyncStarted(HttpServletRequest request) {
        return request.isAsyncStarted();
    }

    public static boolean isAsyncSupported(HttpServletRequest request) {
        return request.isAsyncSupported();
    }

    public static Method getMethod(HttpServletRequest request) {
        return Method.valueOf(request.getMethod().toUpperCase());
    }

    public static ServletInputStream inputStream(HttpServletRequest request) {
        try {
            return request.getInputStream();
        } catch (IOException e) {
            throw new MicroFoxException(e);
        }
    }
}
