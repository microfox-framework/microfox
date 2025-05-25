package ir.moke;

import ir.moke.http.ContentType;
import ir.moke.http.Method;
import ir.moke.http.ResourceHolder;
import ir.moke.http.Route;

public class MicroFox {
    public static void get(String path, Route route) {
        ResourceHolder.instance.add(Method.GET, path, route);
    }

    public static void get(String path, ContentType contentType, Route route) {
        ResourceHolder.instance.add(Method.GET, path, contentType, route);
    }

    public static void post(String path, Route route) {
        ResourceHolder.instance.add(Method.POST, path, route);
    }

    public static void post(String path, ContentType contentType, Route route) {
        ResourceHolder.instance.add(Method.POST, path, contentType, route);
    }

    public static void delete(String path, Route route) {
        ResourceHolder.instance.add(Method.DELETE, path, route);
    }

    public static void delete(String path, ContentType contentType, Route route) {
        ResourceHolder.instance.add(Method.DELETE, path, contentType, route);
    }

    public static void put(String path, Route route) {
        ResourceHolder.instance.add(Method.PUT, path, route);
    }

    public static void put(String path, ContentType contentType, Route route) {
        ResourceHolder.instance.add(Method.PUT, path, contentType, route);
    }

    public static void patch(String path, Route route) {
        ResourceHolder.instance.add(Method.PATCH, path, route);
    }

    public static void patch(String path, ContentType contentType, Route route) {
        ResourceHolder.instance.add(Method.PATCH, path, contentType, route);
    }

    public static void head(String path, Route route) {
        ResourceHolder.instance.add(Method.HEAD, path, route);
    }

    public static void options(String path, Route route) {
        ResourceHolder.instance.add(Method.OPTIONS, path, route);
    }

    public static void trace(String path, Route route) {
        ResourceHolder.instance.add(Method.TRACE, path, route);
    }
}
