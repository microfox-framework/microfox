package ir.moke.microfox.api.mongodb;

import com.mongodb.client.MongoCollection;

public interface MongoProvider {
    <T> MongoCollection<T> collection(String identity, Class<T> entityClass);
}
