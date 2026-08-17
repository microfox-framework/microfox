package ir.moke.microfox.api.http;

@FunctionalInterface
public interface RestConsumer {

    void accept(String path, HttpMethod method, Route route);
}
