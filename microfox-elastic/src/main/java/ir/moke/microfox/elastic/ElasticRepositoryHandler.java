package ir.moke.microfox.elastic;

import ir.moke.microfox.api.elastic.BulkOperation;
import ir.moke.microfox.api.elastic.ElasticCriteria;
import ir.moke.microfox.api.elastic.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ElasticRepositoryHandler<T> implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ElasticRepositoryHandler.class);
    private final String identity;
    private final String index;
    private final Class<T> clazz;

    public ElasticRepositoryHandler(String identity, Class<T> clazz) {
        this.identity = identity;
        this.index = index(clazz);
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        logger.trace("Called: {}, args: {}", name, Arrays.toString(args));
        validateIndex(index);
        switch (name) {
            case "indexCreate" -> ElasticController.createIndex(identity, index, clazz);
            case "indexRefresh" -> ElasticController.refreshIndex(identity, index);
            case "indexDelete" -> ElasticController.deleteIndex(identity, index);
            case "reindex" -> ElasticController.reindex(identity, index, (String) args[0]);
            case "save" -> ElasticController.save(identity, index, (String) args[0], args[1]);
            case "update" -> ElasticController.update(identity, index, (String) args[0], args[1]);
            case "delete" -> ElasticController.delete(identity, index, (String) args[0]);
            case "deleteByQuery" -> ElasticController.deleteByQuery(identity, index, (ElasticCriteria) args[0]);
            case "bulk" -> ElasticController.bulk(identity, index, (List<BulkOperation<T>>) args[0]);
            case "updateMapping" -> ElasticController.updateMapping(identity, index, clazz);
            case "search" -> {
                return ElasticController.search(identity, index, (ElasticCriteria) args[0], clazz);
            }
            case "get" -> {
                return ElasticController.get(identity, index, (String) args[0], clazz);
            }
        }
        return null;
    }

    private static <T> String index(Class<T> clazz) {
        boolean hasIndex = clazz.isAnnotationPresent(Index.class);
        if (hasIndex) {
            return clazz.getDeclaredAnnotation(Index.class).value().toLowerCase();
        } else {
            return clazz.getSimpleName().toLowerCase();
        }
    }

    private static void validateIndex(String index) {
        if (index == null || index.isEmpty()) {
            logger.error("Index name must not be null or empty");
            throw new IllegalArgumentException("Index name must not be null or empty");
        }
    }
}
