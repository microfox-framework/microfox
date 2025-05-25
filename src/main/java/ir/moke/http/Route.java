package ir.moke.http;

@FunctionalInterface
public interface Route {

    void handle(Request request, Response response);
}
