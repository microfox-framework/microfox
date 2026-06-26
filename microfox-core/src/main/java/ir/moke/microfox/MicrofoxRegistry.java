package ir.moke.microfox;

import ir.moke.microfox.api.elastic.ElasticConfig;
import ir.moke.microfox.api.elastic.ElasticProvider;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.HttpProvider;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.kafka.KafkaProvider;
import ir.moke.microfox.api.mongodb.MongoConnectionInfo;
import ir.moke.microfox.api.mongodb.MongoProvider;
import ir.moke.microfox.api.redis.RedisConfig;
import ir.moke.microfox.api.redis.RedisProvider;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.ExceptionMapperHolder;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class MicrofoxRegistry {

    private static final HttpProvider httpProvider = ServiceLoader.load(HttpProvider.class).findFirst().orElse(null);
    private static final JpaProvider jpaProvider = ServiceLoader.load(JpaProvider.class).findFirst().orElse(null);
    private static final JmsProvider jmsProvider = ServiceLoader.load(JmsProvider.class).findFirst().orElse(null);
    private static final KafkaProvider kafkaProvider = ServiceLoader.load(KafkaProvider.class).findFirst().orElse(null);
    private static final ElasticProvider elasticProvider = ServiceLoader.load(ElasticProvider.class).findFirst().orElse(null);
    private static final MongoProvider mongoProvider = ServiceLoader.load(MongoProvider.class).findFirst().orElse(null);
    private static final RedisProvider redisProvider = ServiceLoader.load(RedisProvider.class).findFirst().orElse(null);

    /* Exception */
    public static <T extends Throwable> void exceptionMapperRegister(Class<T> t, ExceptionMapper mapper) {
        ExceptionMapperHolder.add(t, mapper);
    }

    public static <T extends Throwable> void exceptionMapperUnregister(Class<T> t) {
        ExceptionMapperHolder.remove(t);
    }

    /* HTTP */
    public static void removeHttpRoute(String path, HttpMethod method) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.remove(path, method);
    }

    public static void sseRegister(String identity, String path) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.sseRegister(identity, path);
    }

    /* Elastic */
    public static void elasticRegister(String identity, ElasticConfig config) {
        if (elasticProvider == null) throw new UnsupportedOperationException("ElasticSearch support not available");
        elasticProvider.register(identity, config);
    }

    public static void elasticUnregister(String identity) {
        if (elasticProvider == null) throw new UnsupportedOperationException("ElasticSearch support not available");
        elasticProvider.unregister(identity);
    }


    /* Kafka */
    public static <K, V> void kafkaProducerRegister(String clientId, Map<String, Object> config) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.registerProducer(clientId, config);
    }

    public static <K, V> void kafkaConsumerRegister(String clientId, Map<String, Object> config) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.registerConsumer(clientId, config);
    }

    public static <K, V> void kafkaStreamRegister(String clientId, Map<String, Object> config) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.registerStream(clientId, config);
    }

    public static void kafkaProducerUnregister(String clientId) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.unregisterProducer(clientId);
    }

    public static void kafkaConsumerUnregister(String clientId) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.unregisterConsumer(clientId);
    }

    public static void kafkaStreamUnregister(String clientId) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.unregisterStream(clientId);
    }

    /* JMS */
    public static void jmsRegister(String identity, JmsConnectionInfo connectionInfo) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        jmsProvider.register(identity, connectionInfo);
    }

    public static void jmsUnregister(String identity) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        jmsProvider.unregister(identity);
    }

    /* JPA */
    public static void jpaRegisterWithEntities(String identity, Set<Class<?>> entities, Map<String, Object> settings) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.registerWithEntities(identity, entities, settings);
    }

    public static void jpaRegisterWithPackage(String identity, Set<String> scanPackages, Map<String, Object> settings) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.registerWithPackages(identity, scanPackages, settings);
    }

    public static void jpaUnregister(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.unregister(identity);
    }

    /* Mongo */
    public static void mongoRegister(String identity, MongoConnectionInfo connectionInfo) {
        if (mongoProvider == null) throw new UnsupportedOperationException("MongoDB support not available");
        mongoProvider.register(identity, connectionInfo);
    }

    public static void mongoUnregister(String identity) {
        if (mongoProvider == null) throw new UnsupportedOperationException("MongoDB support not available");
        mongoProvider.unregister(identity);
    }

    /* Redis */
    public static void redisRegister(String identity, RedisConfig config) {
        if (redisProvider == null) throw new UnsupportedOperationException("redis support not available");
        redisProvider.register(identity, config);
    }

    public static void redisUnregister(String identity) {
        if (redisProvider == null) throw new UnsupportedOperationException("redis support not available");
        redisProvider.unregister(identity);
    }
}
