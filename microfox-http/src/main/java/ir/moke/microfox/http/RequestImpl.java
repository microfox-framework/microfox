package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.http.validation.MicroFoxValidator;
import ir.moke.microfox.utils.JsonUtils;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.*;

public class RequestImpl implements Request {
    private final HttpServletRequest request;

    public RequestImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String body() {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Locale locale() {
        return request.getLocale();
    }

    @Override
    public <T> T body(Class<T> clazzType) {
        try (ServletInputStream inputStream = request.getInputStream()) {
            String json = new String(inputStream.readAllBytes()).trim();
            T object = JsonUtils.toObject(json, clazzType);
            MicroFoxValidator.validate(object, locale());
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] bodyAsBytes() {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String authType() {
        return request.getAuthType();
    }

    @Override
    public Set<String> headers() {
        Set<String> headers = new HashSet<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            headers.add(enumeration.nextElement());
        }
        return headers;
    }

    @Override
    public String header(String name) {
        return request.getHeader(name);
    }

    @Override
    public Set<String> queryParameters() {
        Set<String> params = new HashSet<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            params.add(enumeration.nextElement());
        }
        return params;
    }

    @Override
    public String queryParameter(String key) {
        return request.getParameter(key);
    }

    @Override
    public String pathParam(String key) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        RouteInfo optionalRouteInfo = HttpUtils.findMatchingRouteInfo(requestURI, Method.valueOf(method.toUpperCase())).get();

        Map<String, String> map = HttpUtils.extractPathParams(optionalRouteInfo.path(), requestURI);
        return map.get(key);

    }

    @Override
    public Map<String, String> cookies() {
        Cookie[] cookies = request.getCookies();
        Map<String, String> map = new HashMap<>();
        for (Cookie cookie : cookies) {
            map.put(cookie.getName(), cookie.getValue());
        }

        return map;
    }

    @Override
    public String cookie(String name) {
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

    @Override
    public String userAgent() {
        return request.getHeader("user-agent");
    }

    @Override
    public String user() {
        return request.getRemoteUser();
    }

    @Override
    public String contentType() {
        return request.getContentType();
    }

    @Override
    public String ip() {
        return request.getRemoteAddr();
    }

    @Override
    public String url() {
        return request.getRequestURL().toString();
    }

    @Override
    public String contextPath() {
        return request.getContextPath();
    }

    @Override
    public String servletPath() {
        return request.getServletPath();
    }

    @Override
    public String pathInfo() {
        return request.getPathInfo();
    }

    @Override
    public int port() {
        return request.getServerPort();
    }

    @Override
    public String scheme() {
        return request.getScheme();
    }

    @Override
    public String host() {
        return request.getHeader("host");
    }

    @Override
    public int contentLength() {
        return request.getContentLength();
    }

    @Override
    public String uri() {
        return request.getRequestURI();
    }

    @Override
    public String protocol() {
        return request.getProtocol();
    }

    @Override
    public void attribute(String attribute, Object value) {
        request.setAttribute(attribute, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T attribute(String attribute) {
        return (T) request.getAttribute(attribute);
    }

    @Override
    public Set<String> attributes() {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = (Enumeration<String>) request.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    @Override
    public AsyncContext asyncContext() {
        return request.getAsyncContext();
    }

    @Override
    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }
}
