package ir.moke.microfox.api.elastic;

import java.util.List;

public interface ElasticRepository<T> {
    void indexCreate();

    void indexRefresh(String index);

    void indexDelete(String index);

    String reindex(String sourceIndex, String destIndex);

    void save(String id, T document);

    String indexAutoId(T document);

    void update(String id, T document);

    void deleteByQuery(ElasticCriteria criteria);

    List<T> search(ElasticCriteria criteria);

    T get(String id);

    void delete(String id);

    void bulk(List<BulkOperation<T>> operations);

    void updateMapping();
}
