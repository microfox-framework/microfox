package ir.moke.microfox.utils;

import ir.moke.microfox.http.FilterInfo;
import ir.moke.microfox.http.Method;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.microfox.http.RouteInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpUtils {
    private static Pattern compilePattern(String pattern) {
        String regex = pattern.replaceAll(":([^/]+)", "([^/]+)");
        return Pattern.compile("^" + regex + "$");
    }

    public static Optional<RouteInfo> findMatchingRouteInfo(String reqPath, Method method) {
        for (RouteInfo routeInfo : ResourceHolder.instance.listRoutes()) {
            String path = routeInfo.path();
            Pattern regex = compilePattern(path);
            if (regex.matcher(reqPath).matches() && method.equals(routeInfo.method())) {
                return Optional.of(routeInfo);
            }
        }
        return Optional.empty();
    }

    public static Optional<FilterInfo> findMatchingFilterInfo(String reqPath) {
        for (FilterInfo filterInfo : ResourceHolder.instance.listFilters()) {
            String path = filterInfo.path();
            Pattern regex = compilePattern(path);
            if (regex.matcher(reqPath).matches()) {
                return Optional.of(filterInfo);
            }
        }
        return Optional.empty();
    }

    public static Map<String, String> extractPathParams(String pattern, String requestPath) {
        Map<String, String> params = new HashMap<>();

        int queryIndex = pattern.indexOf('?');
        if (queryIndex != -1) {
            pattern = pattern.substring(0, queryIndex);
        }

        String[] patternParts = pattern.split("/");
        String[] pathParts = requestPath.split("/");

        if (patternParts.length != pathParts.length) {
            return params; // or throw exception
        }

        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith(":")) {
                String key = patternParts[i].substring(1);
                String value = pathParts[i];
                params.put(key, value);
            }
        }

        return params;
    }

}
