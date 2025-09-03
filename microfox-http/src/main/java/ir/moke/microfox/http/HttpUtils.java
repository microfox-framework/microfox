package ir.moke.microfox.http;

import ir.moke.jsysbox.file.JFile;
import ir.moke.microfox.MicrofoxEnvironment;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.StatusCode;
import jakarta.servlet.ServletOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static Pattern compilePattern(String pattern) {
        String regex = pattern.replaceAll("\\{([^/]+)\\}", "([^/]+)");
        return Pattern.compile("^" + regex + "$");
    }

    public static Optional<RouteInfo> findMatchingRouteInfo(String reqPath, Method method) {
        for (RouteInfo routeInfo : ResourceHolder.listRoutes()) {
            String path = routeInfo.path();
            Pattern regex = compilePattern(path);
            if (regex.matcher(normalizePath(reqPath)).matches() && method.equals(routeInfo.method())) {
                return Optional.of(routeInfo);
            }
        }
        return Optional.empty();
    }

    public static Optional<FilterInfo> findMatchingFilterInfo(String reqPath) {
        for (FilterInfo filterInfo : ResourceHolder.listFilters()) {
            String path = filterInfo.path();
            if (path.endsWith("*") && reqPath.startsWith(path.substring(0, path.length() - 1))) {
                return Optional.of(filterInfo);
            } else if (path.equals(reqPath)) {
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

    public static String concatContextPath(String path) {
        return !MicrofoxEnvironment.getEnv("microfox.http.base.api").equals("/") ? MicrofoxEnvironment.getEnv("microfox.http.base.api") + path : path;
    }

    public static String normalizePath(String path) {
        if (path != null && path.endsWith("/") && path.length() > 1) {
            return path.substring(0, path.length() - 1); // remove trailing slash
        }
        return path;
    }

    public static void loadHtmlContent(Request req, Response resp) {
        String pageDirectory = MicrofoxEnvironment.getEnv("microfox.http.page");
        if (pageDirectory == null || pageDirectory.isEmpty()) {
            logger.warn("microfox.http.page value is null/empty");
            return;
        }
        try {
            String path = req.uri();
            if (path.equals("/")) {
                path = "index.html";
            } else if (path.startsWith("/#/")) {
                path = "index.html";
            } else {
                path = path.substring(1);
            }

            Path filePath = Path.of(pageDirectory).resolve(path);
            if (Files.exists(filePath)) {
                byte[] content = Files.readAllBytes(filePath);

                // MIME Type
                String mimeType = JFile.mime(filePath.toString());
                resp.header("Content-Type", mimeType);

                try (ServletOutputStream outputStream = resp.outputStream()) {
                    outputStream.write(content);
                    outputStream.flush();
                }
            } else {
                sendEmptyResponse(resp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendEmptyResponse(Response response) {
        response.status(StatusCode.NO_CONTENT.getCode());
        response.contentLength(0);
        response.flushBuffer();
    }
}
