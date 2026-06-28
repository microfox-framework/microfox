package ir.moke.microfox.http;

import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.http.*;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.ExceptionMapperHolder;
import ir.moke.microfox.http.proxy.RequestProxy;
import ir.moke.microfox.http.proxy.ResponseProxy;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class HttpHelper extends HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

    public static RouteInfo findMatchingRouteInfo(String reqPath, HttpMethod httpMethod) {
        reqPath = normalizePath(reqPath);
        for (RouteInfo routeInfo : ResourceHolder.listRoutes()) {
            if (!httpMethod.equals(routeInfo.getHttpMethod())) {
                continue;
            }

            Pattern pattern = routeInfo.getPattern();
            if (pattern.matcher(reqPath).matches()) {
                return routeInfo;
            }
        }
        return null;
    }


    public static List<FilterInfo> findMatchingFilterInfo(String reqPath) {
        reqPath = normalizePath(reqPath);
        List<FilterInfo> list = new ArrayList<>();
        for (FilterInfo filterInfo : ResourceHolder.listFilters()) {
            Pattern pattern = filterInfo.getPattern();
            if (pattern.matcher(reqPath).matches()) {
                list.add(filterInfo);
            }
        }
        list.sort(Comparator.comparingInt(FilterInfo::getOrder));
        return list;
    }

    public static String concatContextPath(String path) {
        return !MicroFoxEnvironment.getEnv("microfox.http.base.api").equals("/") ? MicroFoxEnvironment.getEnv("microfox.http.base.api") + path : path;
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

    public static void handleExceptionMapper(HttpServletResponse resp, Throwable t) {
        ExceptionMapper exceptionMapper = ExceptionMapperHolder.get(t.getClass());
        if (exceptionMapper == null) {
            logger.error("Mapper not registered for exception {}", t.getClass());
            internalServerError(resp, t);
        } else {
            try {
                ErrorObject errorObject = exceptionMapper.handle(t);
                if (errorObject != null) {
                    Optional.ofNullable(errorObject.getStatusCode()).ifPresent(item -> resp.setStatus(item.getCode()));
                    Optional.ofNullable(errorObject.getContentType()).ifPresent(item -> resp.setContentType(item.getType()));
                    Optional.ofNullable(errorObject.getHeaders()).ifPresent(item -> fillExtraHeaders(resp, item));
                    Optional.ofNullable(errorObject.getLocale()).ifPresent(resp::setLocale);
                    Optional.ofNullable(errorObject.getCharacterEncoding()).ifPresent(resp::setCharacterEncoding);
                    Optional.ofNullable(errorObject.getCookies()).ifPresent(item -> item.forEach(resp::addCookie));
                    Optional.ofNullable(errorObject.getBody()).ifPresent(item -> sendResponse(resp, item));
                } else {
                    internalServerError(resp, t);
                }
            } catch (Throwable e) {
                handleExceptionMapper(resp, e);
            }
        }
    }

    private static void internalServerError(HttpServletResponse resp, Throwable t) {
        logger.error("Microfox Unknown Error", t);
        resp.setStatus(StatusCode.INTERNAL_SERVER_ERROR.getCode());
        sendResponse(resp, t.getMessage().getBytes(StandardCharsets.UTF_8));
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
