package ir.moke.http;

@FunctionalInterface
public interface Filter {

    void handle(Request request, Response response);
}
