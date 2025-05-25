package ir.moke.utils;

import ir.moke.http.FilterInfo;
import ir.moke.http.Method;
import ir.moke.http.ResourceHolder;
import ir.moke.http.RouteInfo;

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
}
