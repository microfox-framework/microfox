package ir.moke.microfox.api.http;

import jakarta.servlet.ServletException;

import java.io.IOException;

@FunctionalInterface
public interface Route {

    void handle(Request request, Response response) throws IOException, ServletException;
}
