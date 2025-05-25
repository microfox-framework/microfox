package ir.moke.microfox.http;

@FunctionalInterface
public interface Route {

    void handle(Request request, Response response);
}
