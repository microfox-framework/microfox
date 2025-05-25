package ir.moke;

import ir.moke.http.Method;
import ir.moke.http.ResourceHolder;
import ir.moke.http.Route;

public class MicroFox {
    public static void get(String path, Route route) {
        ResourceHolder.instance.add(Method.GET, path, route);
    }

    public static void post(String path, Route route) {
        ResourceHolder.instance.add(Method.POST, path, route);
    }
}
