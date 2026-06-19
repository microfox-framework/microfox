package ir.moke.microfox.api.elastic;

public interface ElasticProvider {
    <T> ElasticRepository<T> elastic(String identity, Class<T> clazz);

    void register(String identity, ElasticConfig config);

    void unregister(String identity);
}
