package ir.moke.microfox.api.http;

@FunctionalInterface
public interface Filter {

    void handle(Request request, Response response);
}
