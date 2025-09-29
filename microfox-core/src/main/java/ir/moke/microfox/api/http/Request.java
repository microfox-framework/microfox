package ir.moke.microfox.api.http;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface Request {
    String body();

    Locale locale();

    <T> T body(Class<T> clazzType);

    byte[] bodyAsBytes();

    String authType();

    Set<String> headers();

    String header(String name);

    Set<String> queryParameters();

    String queryParameter(String key);

    String pathParam(String key);

    Map<String, String> cookies();

    String cookie(String name);

    String userAgent();

    String user();

    String contentType();

    String remoteIp();
    String localIp();

    String url();

    String contextPath();

    String servletPath();

    String pathInfo();

    int port();

    String scheme();

    String host();

    int contentLength();

    String uri();

    String protocol();

    void attribute(String attribute, Object value);

    <T> T attribute(String attribute);

    Set<String> attributes();

    AsyncContext asyncContext();
    AsyncContext startAsync();

    boolean isAsyncStarted();

    boolean isAsyncSupported();

    Method getMethod();

    ServletInputStream inputStream();
}
