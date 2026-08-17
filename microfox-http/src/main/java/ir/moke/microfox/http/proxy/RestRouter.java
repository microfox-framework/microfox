package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.RestConsumer;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;

public class RestRouter {

    private static final Map<Class<?>, HttpMethod> HTTP_METHOD_ANNOTATIONS = Map.of(
            GET.class, HttpMethod.GET,
            POST.class, HttpMethod.POST,
            PUT.class, HttpMethod.PUT,
            DELETE.class, HttpMethod.DELETE,
            PATCH.class, HttpMethod.PATCH
    );

    public static void registerRoutes(Class<?> restClass, RestConsumer consumer) {
        String basePath = Optional.ofNullable(restClass.getAnnotation(Path.class))
                .map(Path::value)
                .orElse("");

        for (Method method : restClass.getDeclaredMethods()) {
            HttpMethod httpMethod = resolveHttpMethod(method);
            if (httpMethod == null) continue;

            String methodPath = Optional.ofNullable(method.getAnnotation(Path.class))
                    .map(Path::value)
                    .orElse("");

            String fullPath = normalizePath(basePath + methodPath);
            method.setAccessible(true);

            Route route = (req, resp) -> {
                Object[] args = resolveArgs(method, req);
                Object result = method.invoke(null, args); // static methods
                if (result != null) resp.body(result); // assumes Response has json()
            };

            consumer.accept(fullPath, httpMethod, route);
        }
    }

    @SuppressWarnings("unchecked")
    private static HttpMethod resolveHttpMethod(Method method) {
        return HTTP_METHOD_ANNOTATIONS.entrySet().stream()
                .filter(e -> method.isAnnotationPresent((Class<? extends Annotation>) e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private static Object[] resolveArgs(Method method, Request req) {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            args[i] = resolveParam(params[i], req);
        }
        return args;
    }

    private static Object resolveParam(Parameter param, Request req) {
        if (param.isAnnotationPresent(PostBody.class)) {
            Class<?> type = param.getType();
            return req.body(type);
        }
        return BeanParamBinder.resolveParameterValue(param, req);
    }

    private static String normalizePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }
}
