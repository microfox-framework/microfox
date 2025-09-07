package ir.moke.microfox.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import ir.moke.microfox.api.mongodb.MongoProvider;
import ir.moke.microfox.exception.MicrofoxException;

public class MongoProviderImpl implements MongoProvider {

    @Override
    public <T> MongoCollection<T> collection(String identity, Class<T> entityClass) {
        MongoDatabase database = MongoFactory.getMongoDatabase(identity);
        return database.getCollection(collectionName(entityClass), entityClass);
    }

    private static <T> String collectionName(Class<T> entityClass) {
        boolean isAnnotated = entityClass.isAnnotationPresent(Collection.class);
        if (!isAnnotated)
            throw new MicrofoxException("Collection class %s is not annotated by Collection.class".formatted(entityClass.getSimpleName()));

        return entityClass.getDeclaredAnnotation(Collection.class).value();
    }
}
