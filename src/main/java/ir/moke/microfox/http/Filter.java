package ir.moke.microfox.http;

@FunctionalInterface
public interface Filter {

    void handle(Request request, Response response);
}
