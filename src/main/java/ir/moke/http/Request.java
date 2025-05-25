package ir.moke.http;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.*;

public class Request {
    private final HttpServletRequest httpServletRequest;

    public Request(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public String body() {
        try (ServletInputStream inputStream = httpServletRequest.getInputStream()) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] bodyAsBytes() {
        try (ServletInputStream inputStream = httpServletRequest.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String authType() {
        return httpServletRequest.getAuthType();
    }

    public Set<String> headers() {
        Set<String> headers = new HashSet<>();
        Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            headers.add(enumeration.nextElement());
        }
        return headers;
    }

    public String header(String name) {
        return httpServletRequest.getHeader(name);
    }

    public Set<String> queryParameters() {
        Set<String> params = new HashSet<>();
        Enumeration<String> enumeration = httpServletRequest.getParameterNames();
        while (enumeration.hasMoreElements()) {
            params.add(enumeration.nextElement());
        }
        return params;
    }

    public String getParameter(String key) {
        return httpServletRequest.getParameter(key);
    }

    public Map<String, String> cookies() {
        Cookie[] cookies = httpServletRequest.getCookies();
        Map<String, String> map = new HashMap<>();
        for (Cookie cookie : cookies) {
            map.put(cookie.getName(), cookie.getValue());
        }

        return map;
    }

    public String cookie(String name) {
        Cookie[] cookies = httpServletRequest.getCookies();
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
        return httpServletRequest.getHeader("user-agent");
    }

    public String user() {
        return httpServletRequest.getRemoteUser();
    }

    public String contentType() {
        return httpServletRequest.getContentType();
    }

    public String ip() {
        return httpServletRequest.getRemoteAddr();
    }

    public String url() {
        return httpServletRequest.getRequestURL().toString();
    }

    public String contextPath() {
        return httpServletRequest.getContextPath();
    }

    public String servletPath() {
        return httpServletRequest.getServletPath();
    }

    public String pathInfo() {
        return httpServletRequest.getPathInfo();
    }

    public int port() {
        return httpServletRequest.getServerPort();
    }

    public String scheme() {
        return httpServletRequest.getScheme();
    }

    public String host() {
        return httpServletRequest.getHeader("host");
    }

    public int contentLength() {
        return httpServletRequest.getContentLength();
    }

    public String uri() {
        return httpServletRequest.getRequestURI();
    }

    public String protocol() {
        return httpServletRequest.getProtocol();
    }

    public void attribute(String attribute, Object value) {
        httpServletRequest.setAttribute(attribute, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T attribute(String attribute) {
        return (T) httpServletRequest.getAttribute(attribute);
    }

    public Set<String> attributes() {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = (Enumeration<String>) httpServletRequest.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }
}
