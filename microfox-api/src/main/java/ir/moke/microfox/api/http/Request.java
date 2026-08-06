package ir.moke.microfox.api.http;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    <U> U queryParameter(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends U> supplier);

    <U> U queryParameter(String key, Predicate<String> predicate, Function<String, ? extends U> parser, U u);

    <U> U queryParameterThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends Throwable> supplier);

    <U> U queryParameterThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, String message);

    String pathParam(String key);

    <U> U pathParam(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends U> supplier);

    <U> U pathParam(String key, Predicate<String> predicate, Function<String, ? extends U> parser, U u);

    <U> U pathParamThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, Supplier<? extends Throwable> supplier);

    <U> U pathParamThrowable(String key, Predicate<String> predicate, Function<String, ? extends U> parser, String message);

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

    HttpMethod getMethod();

    ServletInputStream inputStream();

    Principal principal();

    String remoteUser();
}
