package ir.moke.microfox.mongodb;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ConnectionPoolSettings;
import ir.moke.microfox.exception.MicroFoxException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoFactory {
    private static final Logger logger = LoggerFactory.getLogger(MongoFactory.class);
    private static final Map<String, MongoClient> MONGO_CLIENTS = new ConcurrentHashMap<>();
    private static final Map<String, MongoConnectionInfo> MONGO_CONNECTION_INFOS = new ConcurrentHashMap<>();

    public static void registerMongoDatabase(String identity, MongoConnectionInfo info) {
        String username = info.getUsername();
        String password = info.getPassword();
        String host = info.getHost();
        int port = info.getPort();
        String databaseName = info.getDatabaseName();
        String connectionMetadata = info.getConnectionMetadata();
        String connectionURL = "mongodb://%s:%s@%s:%s/%s?%s".formatted(username, password, host, port, databaseName, connectionMetadata);
        ConnectionString connString = new ConnectionString(connectionURL);

        CodecRegistry fromProviders = fromProviders(
                new Jsr310CodecProvider(),
                PojoCodecProvider.builder().automatic(true).build()
        );

        CodecRegistry pojoRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .codecRegistry(pojoRegistry)
                .applyToConnectionPoolSettings(builder -> getBuilder(builder, info))
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        MONGO_CLIENTS.put(identity, mongoClient);
        MONGO_CONNECTION_INFOS.put(identity, info);
        logger.info("Mongo database successfully registered");
    }

    private static void getBuilder(ConnectionPoolSettings.Builder builder, MongoConnectionInfo info) {
        builder
                .maxSize(info.getPoolMax())
                .minSize(info.getPoolMin())
                .maxWaitTime(info.getPoolMaxWaitTime(), TimeUnit.SECONDS)
                .maxConnectionIdleTime(info.getPoolMaxConnectionIdleTime(), TimeUnit.SECONDS);
    }

    public static MongoClient getMongoClient(String identity) {
        MongoClient mongoClient = MONGO_CLIENTS.get(identity);
        if (mongoClient == null) {
            throw new MicroFoxException("No MongoClient registered with identity: " + identity);
        }

        return mongoClient;
    }

    public static MongoDatabase getMongoDatabase(String identity) {
        MongoConnectionInfo info = MONGO_CONNECTION_INFOS.get(identity);
        if (info == null || info.getDatabaseName() == null || info.getDatabaseName().isEmpty()) {
            throw new MicroFoxException("Database name not registered with identity: " + identity);
        }

        MongoClient mongoClient = getMongoClient(identity);
        return mongoClient.getDatabase(info.getDatabaseName());
    }
}