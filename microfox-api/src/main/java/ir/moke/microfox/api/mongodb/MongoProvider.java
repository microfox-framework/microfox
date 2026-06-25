package ir.moke.microfox.api.mongodb;

import com.mongodb.client.MongoCollection;

public interface MongoProvider {
    <T> MongoCollection<T> collection(String identity, Class<T> entityClass);

    void register(String identity, MongoConnectionInfo connectionInfo);

    void unregister(String identity);
}
