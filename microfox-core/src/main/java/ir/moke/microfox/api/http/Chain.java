package ir.moke.microfox.api.http;

public interface Chain {
    void doFilter(Request request, Response response);
}
