package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaStreamController;
import ir.moke.microfox.api.kafka.KafkaStreamState;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class KafkaStreamFactory {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamFactory.class);
    private static final Map<String, Map<String, Object>> CONFIGS = new HashMap<>();
    private static final Map<String, KafkaStreams> STREAMS_MAP = new ConcurrentHashMap<>();
    private static final Map<String, CopyOnWriteArrayList<BiConsumer<KafkaStreamState, KafkaStreamState>>> LISTENERS = new ConcurrentHashMap<>();

    static {
        shutdownHook();
    }

    public static void register(String identity, Map<String, Object> configs) {
        if (identity == null)
            throw new MicroFoxException("identity could not be null");
        if (configs == null)
            throw new MicroFoxException("config map could not be null");
        if (isAlreadyExists(identity))
            throw new MicroFoxException("Stream %s already registered".formatted(identity));

        CONFIGS.put(identity, configs);
        LISTENERS.put(identity, new CopyOnWriteArrayList<>());
    }

    static KafkaStreams buildStreams(String identity, Topology topology) {
        Map<String, Object> configs = getConfig(identity);

        Properties properties = new Properties();
        properties.putAll(configs);
        KafkaStreams streams = new KafkaStreams(topology, properties);

        // register internal state listener that delegates to registered listeners
        streams.setStateListener((newState, oldState) -> {
            List<BiConsumer<KafkaStreamState, KafkaStreamState>> l = LISTENERS.get(identity);
            if (l != null) {
                for (BiConsumer<KafkaStreamState, KafkaStreamState> c : l) {
                    try {
                        c.accept(KafkaStreamState.valueOf(newState.name()), KafkaStreamState.valueOf(oldState.name()));
                    } catch (Exception e) {
                        // swallow listener exceptions but log
                        logger.warn("State listener threw", e);
                    }
                }
            }
        });

        return streams;
    }

    public static KafkaStreams get(String clientId) {
        KafkaStreams streams = STREAMS_MAP.get(clientId);
        if (streams == null) throw new MicroFoxException("No KafkaStreams for clientId: " + clientId);
        return streams;
    }

    static void replaceStreams(String clientId, KafkaStreams streams) {
        STREAMS_MAP.put(clientId, streams);
    }

    public static void addStateListener(String clientId, BiConsumer<KafkaStreamState, KafkaStreamState> listener) {
        LISTENERS.get(clientId).add(listener);
    }

    public static void removeStateListener(String clientId, BiConsumer<KafkaStreamState, KafkaStreamState> listener) {
        LISTENERS.get(clientId).remove(listener);
    }

    public static void close(String clientId, Duration duration) {
        CONFIGS.remove(clientId);
        LISTENERS.remove(clientId).clear();
        KafkaStreams kafkaStreams = STREAMS_MAP.remove(clientId);
        if (kafkaStreams != null) {
            kafkaStreams.close(duration);
            kafkaStreams.cleanUp();
        }
    }

    public static void closeAll() {
        STREAMS_MAP.keySet().forEach(item -> close(item, null));
    }

    public static KafkaStreamController createProxyInstance(String clientId, Topology topology) {
        return (KafkaStreamController) Proxy.newProxyInstance(
                KafkaStreamController.class.getClassLoader(),
                new Class<?>[]{KafkaStreamController.class},
                new KafkaStreamHandler(clientId, topology)
        );
    }

    public static void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(KafkaStreamFactory::closeAll, "kafka-consumer-shutdown"));
    }

    private static boolean isAlreadyExists(String identity) {
        return CONFIGS.containsKey(identity);
    }

    private static Map<String, Object> getConfig(String clientId) {
        Map<String, Object> config = CONFIGS.get(clientId);
        if (config == null) throw new MicroFoxException("No topology/props for: " + clientId);
        return config;
    }
}
