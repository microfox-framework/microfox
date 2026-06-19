package ir.moke.microfox.elastic;

import ir.moke.microfox.api.elastic.ElasticConfig;
import ir.moke.microfox.api.elastic.ElasticProvider;
import ir.moke.microfox.api.elastic.ElasticRepository;

public class ElasticProviderImpl implements ElasticProvider {
    @Override
    public <T> ElasticRepository<T> elastic(String identity, Class<T> clazz) {
        return ElasticFactory.create(identity, clazz);
    }

    @Override
    public void register(String identity, ElasticConfig config) {
        ElasticFactory.register(identity, config);
    }

    @Override
    public void unregister(String identity) {
        ElasticFactory.unregister(identity);
    }
}
