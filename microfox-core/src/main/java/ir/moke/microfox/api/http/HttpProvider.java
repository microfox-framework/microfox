package ir.moke.microfox.api.http;

public interface HttpProvider {
    void filter(String path, Filter... filters);

    void get(String path, Route route);

    void post(String path, Route route);

    void delete(String path, Route route);

    void put(String path, Route route);

    void patch(String path, Route route);

    void head(String path, Route route);

    void options(String path, Route route);

    void trace(String path, Route route);
}
