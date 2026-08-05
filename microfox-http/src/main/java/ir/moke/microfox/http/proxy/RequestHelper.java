package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.exception.MicroFoxParameterException;
import ir.moke.microfox.http.HttpHelper;
import ir.moke.microfox.http.validation.MicroFoxValidator;
import ir.moke.utils.JsonUtils;
import ir.moke.utils.OptionalObject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    public static <U> U queryParameter(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends U> supplier, HttpServletRequest request) {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrGet(predicate, parser, supplier);
    }

    public static <U> U queryParameter(String key, Predicate<String> predicate, Function<String, ? extends U> parser, U u, HttpServletRequest request) {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrGet(predicate, parser, u);
    }

    public static <U> U queryParameterThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends Throwable> supplier, HttpServletRequest request) throws Throwable {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrThrows(predicate, parser, supplier);
    }

    public static <U> U queryParameterThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, String message, HttpServletRequest request) throws Throwable {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrThrows(predicate, parser, () -> new MicroFoxParameterException(message));
    }

    public static String pathParam(String key, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        RouteInfo routeInfo = HttpHelper.findMatchingRouteInfo(requestURI, HttpMethod.valueOf(method.toUpperCase()));
        if (routeInfo == null) return null;
        Map<String, String> map = HttpHelper.extractPathParams(routeInfo.getPath(), requestURI);
        return map.get(key);
    }

    public static <U> U pathParam(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends U> supplier, HttpServletRequest request) {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrGet(predicate, parser, supplier);
    }

    public static <U> U pathParam(String key, Predicate<String> predicate, Function<String, ? extends U> parser, U u, HttpServletRequest request) {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrGet(predicate, parser, u);
    }

    public static <U> U pathParamThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends Throwable> supplier, HttpServletRequest request) throws Throwable {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrThrows(predicate, parser, supplier);
    }

    public static <U> U pathParamThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, String message, HttpServletRequest request) throws Throwable {
        String value = request.getParameter(key);
        return OptionalObject.of(value).parseOrThrows(predicate, parser, () -> new MicroFoxParameterException(message));
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

    public static HttpMethod getMethod(HttpServletRequest request) {
        return HttpMethod.valueOf(request.getMethod().toUpperCase());
    }

    public static ServletInputStream inputStream(HttpServletRequest request) {
        try {
            return request.getInputStream();
        } catch (IOException e) {
            throw new MicroFoxException(e);
        }
    }

    public static Map<String, String[]> headersMap(HttpServletRequest request) {
        Map<String, String[]> headers = new LinkedHashMap<>();
        Collections.list(request.getHeaderNames()).forEach(name -> headers.put(name, Collections.list(request.getHeaders(name)).toArray(String[]::new)));
        return headers;
    }

    public static Map<String, String[]> queryParametersMap(HttpServletRequest req) {
        return req.getParameterMap();
    }
}
