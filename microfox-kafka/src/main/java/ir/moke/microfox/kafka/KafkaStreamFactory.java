package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaStreamController;
import ir.moke.microfox.api.kafka.KafkaStreamState;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class KafkaStreamFactory {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamFactory.class);
    private static final Map<String, Topology> TOPOLOGY_MAP = new ConcurrentHashMap<>();
    private static final List<Map<String, Object>> CONFIGS = new ArrayList<>();
    private static final Map<String, KafkaStreams> STREAMS_MAP = new ConcurrentHashMap<>();
    private static final Map<String, CopyOnWriteArrayList<BiConsumer<KafkaStreamState, KafkaStreamState>>> LISTENERS = new ConcurrentHashMap<>();

    static {
        shutdownHook();
    }

    public static void register(String clientId, Topology topology, Map<String, Object> configs) {
        if (TOPOLOGY_MAP.containsKey(clientId)) {
            throw new MicroFoxException("Stream %s already registered".formatted(clientId));
        }
        TOPOLOGY_MAP.put(clientId, topology);
        CONFIGS.add(configs);
        LISTENERS.put(clientId, new CopyOnWriteArrayList<>());
        STREAMS_MAP.put(clientId, buildStreams(clientId));
    }

    static KafkaStreams buildStreams(String clientId) {
        Topology topology = TOPOLOGY_MAP.get(clientId);
        Map<String, Object> configs = getProperties(clientId);

        Properties properties = new Properties();
        properties.putAll(configs);
        KafkaStreams streams = new KafkaStreams(topology, properties);

        // register internal state listener that delegates to registered listeners
        streams.setStateListener((newState, oldState) -> {
            List<BiConsumer<KafkaStreamState, KafkaStreamState>> l = LISTENERS.get(clientId);
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

    public static Topology getTopology(String clientId) {
        return TOPOLOGY_MAP.get(clientId);
    }

    public static void close(String clientId) {
        KafkaStreams kafkaStreams = STREAMS_MAP.remove(clientId);
        if (kafkaStreams != null) {
            kafkaStreams.close();
            kafkaStreams.cleanUp();
        }
    }

    public static void closeAll() {
        STREAMS_MAP.keySet().forEach(KafkaStreamFactory::close);
    }

    public static KafkaStreamController createProxyInstance(String clientId) {
        return (KafkaStreamController) Proxy.newProxyInstance(
                KafkaStreamController.class.getClassLoader(),
                new Class<?>[]{KafkaStreamController.class},
                new KafkaStreamHandler(clientId)
        );
    }

    public static void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(KafkaStreamFactory::closeAll, "kafka-consumer-shutdown"));
    }

    private static boolean isAlreadyExists(String clientID) {
        return CONFIGS.stream()
                .map(item -> item.get(ProducerConfig.CLIENT_ID_CONFIG))
                .map(String::valueOf)
                .anyMatch(item -> item.equalsIgnoreCase(clientID));
    }

    private static Map<String, Object> getProperties(String clientId) {
        return CONFIGS.stream()
                .filter(item -> item.get(ProducerConfig.CLIENT_ID_CONFIG).equals(clientId))
                .findFirst()
                .orElseThrow(() -> new MicroFoxException("No topology/props for: " + clientId));
    }
}
