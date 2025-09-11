package ir.moke.microfox.http;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RequestProxy implements InvocationHandler {
    private final HttpServletRequest request;

    public RequestProxy(HttpServletRequest request) {
        this.request = request;
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
            case "locale" -> {
                return RequestHelper.locale(request);
            }
            case "body" -> {
                return invokeBody(args);
            }
            case "bodyAsBytes" -> {
                return RequestHelper.bodyAsBytes(request);
            }
            case "authType" -> {
                return RequestHelper.authType(request);
            }
            case "headers" -> {
                return RequestHelper.headers(request);
            }
            case "header" -> {
                return RequestHelper.header((String) args[0], request);
            }
            case "queryParameters" -> {
                return RequestHelper.queryParameters(request);
            }
            case "queryParameter" -> {
                return RequestHelper.queryParameter((String) args[0], request);
            }
            case "pathParam" -> {
                return RequestHelper.pathParam((String) args[0], request);
            }
            case "cookies" -> {
                return RequestHelper.cookies(request);
            }
            case "cookie" -> {
                return RequestHelper.cookie((String) args[0], request);
            }
            case "userAgent" -> {
                return RequestHelper.userAgent(request);
            }
            case "user" -> {
                return RequestHelper.user(request);
            }
            case "contentType" -> {
                return RequestHelper.contentType(request);
            }
            case "remoteIp" -> {
                return RequestHelper.remoteIp(request);
            }
            case "localIp" -> {
                return RequestHelper.localIp(request);
            }
            case "url" -> {
                return RequestHelper.url(request);
            }
            case "contextPath" -> {
                return RequestHelper.contextPath(request);
            }
            case "servletPath" -> {
                return RequestHelper.servletPath(request);
            }
            case "pathInfo" -> {
                return RequestHelper.pathInfo(request);
            }
            case "port" -> {
                return RequestHelper.port(request);
            }
            case "scheme" -> {
                return RequestHelper.scheme(request);
            }
            case "host" -> {
                return RequestHelper.host(request);
            }
            case "contentLength" -> {
                return RequestHelper.contentLength(request);
            }
            case "uri" -> {
                return RequestHelper.uri(request);
            }
            case "protocol" -> {
                return RequestHelper.protocol(request);
            }
            case "getAttribute" -> {
                return RequestHelper.getAttribute((String) args[0], request);
            }
            case "setAttribute" -> RequestHelper.setAttribute((String) args[0], args[1], request);
            case "attributes" -> {
                return RequestHelper.attributes(request);
            }
            case "asyncContext" -> {
                return RequestHelper.asyncContext(request);
            }
            case "isAsyncStarted" -> {
                return RequestHelper.isAsyncStarted(request);
            }
            case "isAsyncSupported" -> {
                return RequestHelper.isAsyncSupported(request);
            }
            case "getMethod" -> {
                return RequestHelper.getMethod(request);
            }
            case "inputStream" -> {
                return RequestHelper.inputStream(request);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Object invokeBody(Object[] args) {
        if (args == null) return RequestHelper.body(request);
        return RequestHelper.body((Class<T>) args[0], request);
    }
}
