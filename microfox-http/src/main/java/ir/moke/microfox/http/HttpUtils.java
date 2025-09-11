package ir.moke.microfox.http;

import ir.moke.jsysbox.file.JFile;
import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.http.*;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.ExceptionMapperHolder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
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
        return !MicroFoxEnvironment.getEnv("microfox.http.base.api").equals("/") ? MicroFoxEnvironment.getEnv("microfox.http.base.api") + path : path;
    }

    public static String normalizePath(String path) {
        if (path != null && path.endsWith("/") && path.length() > 1) {
            return path.substring(0, path.length() - 1); // remove trailing slash
        }
        return path;
    }

    public static void loadHtmlContent(Request req, Response resp) {
        String pageDirectory = MicroFoxEnvironment.getEnv("microfox.http.html-directory");
        if (pageDirectory == null || pageDirectory.isEmpty()) {
            logger.warn("page-directory value is null/empty");
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

    public static Request getRequest(HttpServletRequest request) {
        return (Request) Proxy.newProxyInstance(Request.class.getClassLoader(), new Class<?>[]{Request.class}, new RequestProxy(request));
    }

    public static Response getResponse(HttpServletResponse response) {
        return (Response) Proxy.newProxyInstance(Response.class.getClassLoader(), new Class<?>[]{Response.class}, new ResponseProxy(response));
    }

    public static void handleExceptionMapper(HttpServletResponse resp, Exception e) {
        ExceptionMapper<Throwable> mapper = ExceptionMapperHolder.get(e);
        if (mapper != null) {
            ErrorObject ro = mapper.toResponse(e);
            Optional.ofNullable(ro.getStatusCode()).ifPresent(item -> resp.setStatus(item.getCode()));
            Optional.ofNullable(ro.getContentType()).ifPresent(item -> resp.setContentType(item.getType()));
            Optional.ofNullable(ro.getHeaders()).ifPresent(item -> fillExtraHeaders(resp, item));
            Optional.ofNullable(ro.getLocale()).ifPresent(resp::setLocale);
            Optional.ofNullable(ro.getCharacterEncoding()).ifPresent(resp::setCharacterEncoding);
            Optional.ofNullable(ro.getCookies()).ifPresent(item -> item.forEach(resp::addCookie));
            Optional.ofNullable(ro.getBody()).ifPresent(item -> sendResponse(resp, item));
        } else {
            logger.error("Microfox Unknown Error", e);
            resp.setStatus(StatusCode.INTERNAL_SERVER_ERROR.getCode());
            sendResponse(resp, e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void fillExtraHeaders(HttpServletResponse resp, Map<String, Object> headers) {
        headers.forEach((k, v) -> {
            if (v instanceof Integer i) resp.addIntHeader(k, i);
            if (v instanceof Long l) resp.addDateHeader(k, l);
            if (v instanceof String s) resp.addHeader(k, s);
        });
    }

    public static void sendResponse(HttpServletResponse resp, byte[] bytes) {
        try {
            ServletOutputStream outputStream = resp.getOutputStream();
            outputStream.write(bytes);
        } catch (IOException io) {
            logger.error("Microfox IO Error", io);
        }
    }
}
