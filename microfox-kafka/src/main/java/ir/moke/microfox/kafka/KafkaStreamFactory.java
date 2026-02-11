package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaStreamController;
import ir.moke.microfox.api.kafka.KafkaStreamState;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class KafkaStreamFactory {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamFactory.class);
    private static final Map<String, Topology> TOPOLOGY_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Properties> PROP_MAP = new ConcurrentHashMap<>();
    private static final Map<String, KafkaStreams> STREAMS_MAP = new ConcurrentHashMap<>();
    private static final Map<String, CopyOnWriteArrayList<BiConsumer<KafkaStreamState, KafkaStreamState>>> LISTENERS = new ConcurrentHashMap<>();

    static {
        shutdownHook();
    }

    public static void register(String identity, Topology topology, Properties props) {
        if (TOPOLOGY_MAP.containsKey(identity)) {
            throw new MicroFoxException("Stream %s already registered".formatted(identity));
        }
        TOPOLOGY_MAP.put(identity, topology);
        PROP_MAP.put(identity, props);
        LISTENERS.put(identity, new CopyOnWriteArrayList<>());
        STREAMS_MAP.put(identity, buildStreams(identity));
    }

    static KafkaStreams buildStreams(String identity) {
        Topology topology = TOPOLOGY_MAP.get(identity);
        Properties props = PROP_MAP.get(identity);
        if (topology == null || props == null) throw new MicroFoxException("No topology/props for: " + identity);

        KafkaStreams streams = new KafkaStreams(topology, props);

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

    public static KafkaStreams get(String identity) {
        KafkaStreams streams = STREAMS_MAP.get(identity);
        if (streams == null) throw new MicroFoxException("No KafkaStreams for identity: " + identity);
        return streams;
    }

    static void replaceStreams(String identity, KafkaStreams streams) {
        STREAMS_MAP.put(identity, streams);
    }

    public static void addStateListener(String identity, BiConsumer<KafkaStreamState, KafkaStreamState> listener) {
        LISTENERS.get(identity).add(listener);
    }

    public static void removeStateListener(String identity, BiConsumer<KafkaStreamState, KafkaStreamState> listener) {
        LISTENERS.get(identity).remove(listener);
    }

    public static Topology getTopology(String identity) {
        return TOPOLOGY_MAP.get(identity);
    }

    public static Properties getProps(String identity) {
        return PROP_MAP.get(identity);
    }

    public static void close(String identity) {
        KafkaStreams kafkaStreams = STREAMS_MAP.remove(identity);
        if (kafkaStreams != null) {
            kafkaStreams.close();
            kafkaStreams.cleanUp();
        }
    }

    public static void closeAll() {
        STREAMS_MAP.keySet().forEach(KafkaStreamFactory::close);
    }

    public static KafkaStreamController createProxyInstance(String identity) {
        return (KafkaStreamController) Proxy.newProxyInstance(
                KafkaStreamController.class.getClassLoader(),
                new Class<?>[]{KafkaStreamController.class},
                new KafkaStreamHandler(identity)
        );
    }

    public static void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(KafkaStreamFactory::closeAll, "kafka-consumer-shutdown"));
    }
}
