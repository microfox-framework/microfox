package ir.moke.microfox.http;

import ir.moke.microfox.utils.HttpUtils;
import ir.moke.microfox.utils.JsonUtils;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.*;

public class Request {
    private final HttpServletRequest request;

    public Request(HttpServletRequest request) {
        this.request = request;
    }

    public String body() {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T body(Class<T> clazzType) {
        try (ServletInputStream inputStream = request.getInputStream()) {
            String json = new String(inputStream.readAllBytes());
            return JsonUtils.toObject(json, clazzType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] bodyAsBytes() {
        try (ServletInputStream inputStream = request.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String authType() {
        return request.getAuthType();
    }

    public Set<String> headers() {
        Set<String> headers = new HashSet<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            headers.add(enumeration.nextElement());
        }
        return headers;
    }

    public String header(String name) {
        return request.getHeader(name);
    }

    public Set<String> queryParameters() {
        Set<String> params = new HashSet<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            params.add(enumeration.nextElement());
        }
        return params;
    }

    public String queryParameter(String key) {
        return request.getParameter(key);
    }

    public String pathParam(String key) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        RouteInfo optionalRouteInfo = HttpUtils.findMatchingRouteInfo(requestURI, Method.valueOf(method.toUpperCase())).get();

        Map<String, String> map = HttpUtils.extractPathParams(optionalRouteInfo.path(), requestURI);
        return map.get(key);

    }

    public Map<String, String> cookies() {
        Cookie[] cookies = request.getCookies();
        Map<String, String> map = new HashMap<>();
        for (Cookie cookie : cookies) {
            map.put(cookie.getName(), cookie.getValue());
        }

        return map;
    }

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

    public String userAgent() {
        return request.getHeader("user-agent");
    }

    public String user() {
        return request.getRemoteUser();
    }

    public String contentType() {
        return request.getContentType();
    }

    public String ip() {
        return request.getRemoteAddr();
    }

    public String url() {
        return request.getRequestURL().toString();
    }

    public String contextPath() {
        return request.getContextPath();
    }

    public String servletPath() {
        return request.getServletPath();
    }

    public String pathInfo() {
        return request.getPathInfo();
    }

    public int port() {
        return request.getServerPort();
    }

    public String scheme() {
        return request.getScheme();
    }

    public String host() {
        return request.getHeader("host");
    }

    public int contentLength() {
        return request.getContentLength();
    }

    public String uri() {
        return request.getRequestURI();
    }

    public String protocol() {
        return request.getProtocol();
    }

    public void attribute(String attribute, Object value) {
        request.setAttribute(attribute, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T attribute(String attribute) {
        return (T) request.getAttribute(attribute);
    }

    public Set<String> attributes() {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = (Enumeration<String>) request.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }
}
