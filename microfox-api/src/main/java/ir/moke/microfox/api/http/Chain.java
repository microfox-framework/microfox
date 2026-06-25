package ir.moke.microfox.api.http;

@FunctionalInterface
public interface Chain {
    void doFilter(Request request, Response response);
}
