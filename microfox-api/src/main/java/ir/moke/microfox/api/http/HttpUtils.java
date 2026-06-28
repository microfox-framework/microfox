package ir.moke.microfox.api.http;

import java.util.*;
import java.util.regex.Pattern;

public class HttpUtils {

    public static Pattern compilePattern(String pattern) {
        pattern = normalizePath(pattern);
        String regex = pattern.replaceAll("\\{[^/]+}", "[^/]+");
        regex = regex.replace("*", ".*");
        return Pattern.compile("^" + regex + "$");
    }

    public static String normalizePath(String path) {
        if (path != null && path.endsWith("/") && path.length() > 1) {
            return path.substring(0, path.length() - 1); // remove trailing slash
        }
        return path;
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
            return params;
        }

        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) {
                String key = patternParts[i].substring(1, patternParts[i].length() - 1);
                String value = pathParts[i];
                params.put(key, value);
            }
        }

        return params;
    }
}
