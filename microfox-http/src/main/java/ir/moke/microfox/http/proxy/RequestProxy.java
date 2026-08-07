package ir.moke.microfox.http.proxy;

import ir.moke.microfox.http.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
                return handleQueryParameter(args);
            }
            case "queryParameterThrowable" -> {
                return handleQueryParameterThrowable(args);
            }
            case "pathParam" -> {
                return handlePathParameter(args);
            }
            case "pathParamThrowable" -> {
                return handlePathParameterThrowable(args);
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
            case "startAsync" -> {
                return RequestHelper.startAsync(request);
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
            case "principal" -> {
                return SecurityContext.principal();
            }
            case "remoteUser" -> {
                return SecurityContext.principal().getName();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <U> U handlePathParameter(Object[] args) {
        if (args.length == 1) {
            return (U) RequestHelper.pathParam((String) args[0], request);
        } else if (args[3] != null && Supplier.class.isAssignableFrom(args[3].getClass())) {
            Predicate<String> predicate = (Predicate<String>) args[1];
            Function<String, ? extends U> parser = (Function<String, ? extends U>) args[2];
            Supplier<? extends U> supplier = (Supplier<? extends U>) args[3];
            return RequestHelper.pathParam((String) args[0], predicate, parser, supplier, request);
        } else {
            Predicate<String> predicate = (Predicate<String>) args[1];
            Function<String, ? extends U> parser = (Function<String, ? extends U>) args[2];
            U u = (U) args[3];
            return RequestHelper.pathParam((String) args[0], predicate, parser, u, request);
        }
    }

    @SuppressWarnings("unchecked")
    private <U> U handlePathParameterThrowable(Object[] args) throws Throwable {
        Predicate<String> predicate = (Predicate<String>) args[1];
        Function<String, ? extends U> parser = (Function<String, ? extends U>) args[2];

        if (args[3] != null && Supplier.class.isAssignableFrom(args[3].getClass())) {
            Supplier<? extends Throwable> supplier = (Supplier<? extends Throwable>) args[3];
            return RequestHelper.pathParamThrowable((String) args[0], predicate, parser, supplier, request);
        } else {
            String msg = (String) args[3];
            return RequestHelper.pathParamThrowable((String) args[0], predicate, parser, msg, request);
        }
    }

    @SuppressWarnings("unchecked")
    private <U> U handleQueryParameter(Object[] args) {
        if (args.length == 1) {
            return (U) RequestHelper.queryParameter((String) args[0], request);
        } else if (args[3] != null && Supplier.class.isAssignableFrom(args[3].getClass())) {
            Predicate<String> predicate = (Predicate<String>) args[1];
            Function<String, ? extends U> parser = (Function<String, ? extends U>) args[2];
            Supplier<? extends U> supplier = (Supplier<? extends U>) args[3];
            return RequestHelper.queryParameter((String) args[0], predicate, parser, supplier, request);
        } else {
            Predicate<String> predicate = (Predicate<String>) args[1];
            Function<String, ? extends U> parser = (Function<String, ? extends U>) args[2];
            U u = (U) args[3];
            return RequestHelper.queryParameter((String) args[0], predicate, parser, u, request);
        }
    }

    @SuppressWarnings("unchecked")
    private <U> U handleQueryParameterThrowable(Object[] args) throws Throwable {
        Predicate<String> predicate = (Predicate<String>) args[1];
        Function<String, ? extends U> parser = (Function<String, ? extends U>) args[2];

        if (args[3] != null && Supplier.class.isAssignableFrom(args[3].getClass())) {
            Supplier<? extends Throwable> supplier = (Supplier<? extends Throwable>) args[3];
            return RequestHelper.queryParameterThrowable((String) args[0], predicate, parser, supplier, request);
        } else {
            String msg = (String) args[3];
            return RequestHelper.queryParameterThrowable((String) args[0], predicate, parser, msg, request);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Object invokeBody(Object[] args) {
        if (args == null) return RequestHelper.body(request);
        return RequestHelper.body((Class<T>) args[0], request);
    }
}
