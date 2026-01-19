package ir.moke.microfox.api.http;

@FunctionalInterface
public interface Route {

    void handle(Request request, Response response);
}
