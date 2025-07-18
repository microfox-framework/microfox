package ir.moke.microfox.elastic;

import ir.moke.microfox.api.elastic.ElasticRepository;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ElasticFactory {
    private static final Map<String, ElasticConfig> ELASTIC_MAP = new HashMap<>();

    public static void register(String identity, ElasticConfig config) {
        ELASTIC_MAP.put(identity, config);
    }

    public static ElasticConfig getConfig(String identity) {
        return ELASTIC_MAP.get(identity);
    }

    @SuppressWarnings("unchecked")
    public static <T> ElasticRepository<T> create(String identity, Class<T> clazz) {
        return (ElasticRepository<T>) Proxy.newProxyInstance(
                ElasticRepository.class.getClassLoader(),
                new Class<?>[]{ElasticRepository.class},
                new ElasticRepositoryHandler<T>(identity, clazz)
        );
    }
}
