import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.kafka.KafkaStreamController;
import ir.moke.microfox.api.kafka.KafkaStreamState;
import ir.moke.microfox.kafka.KafkaStreamFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class KafkaStreamTest {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamTest.class);
    private static final String IDENTITY = "stream-test-advanced";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9092;

    static {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("input-topic")
                .mapValues(v -> v.toString().toUpperCase())
                .to("output-topic");
        Topology topology = builder.build();

        Map<String, Object> configs = new HashMap<>();
        configs.put(StreamsConfig.CLIENT_ID_CONFIG, IDENTITY);
        configs.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "%s:%s".formatted(HOST, PORT));
        configs.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app-advanced");
        configs.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        KafkaStreamFactory.register(IDENTITY, topology, configs);
    }

    public static void main(String[] args) {
        MicroFox.kafkaStream(IDENTITY, KafkaStreamController::start);
        MicroFox.kafkaStream(IDENTITY, controller -> {
            // add state listener
            BiConsumer<KafkaStreamState, KafkaStreamState> listener = (newState, oldState) ->
                    System.out.println("State changed: " + oldState + " -> " + newState);
            controller.addStateListener(listener);
            // set uncaught exception handler
            controller.setUncaughtExceptionHandler((t, e) -> {
                logger.error("Kafka Error", e);
                // optionally restart
                controller.restart();
            });

            // start stream
            controller.start();

            // later: controller.restart();
            // later: controller.close(Duration.ofSeconds(30));
        });
    }
}

