package ir.moke.microfox.elastic;

import ir.moke.microfox.api.elastic.ElasticProvider;
import ir.moke.microfox.api.elastic.ElasticRepository;

public class ElasticProviderImpl implements ElasticProvider {
    @Override
    public <T> ElasticRepository<T> elastic(String identity, Class<T> clazz) {
        return ElasticFactory.create(identity,clazz);
    }
}
