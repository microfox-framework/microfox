package ir.moke.http;

import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface Route {

    void handle(Request request, HttpServletResponse response);
}
