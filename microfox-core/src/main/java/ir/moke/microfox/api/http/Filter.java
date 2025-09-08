package ir.moke.microfox.api.http;

@FunctionalInterface
public interface Filter {

    boolean handle(Request request, Response response);
}
