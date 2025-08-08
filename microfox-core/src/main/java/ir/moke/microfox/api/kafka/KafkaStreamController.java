package ir.moke.microfox.api.kafka;

import java.time.Duration;
import java.util.function.BiConsumer;

public interface KafkaStreamController {
    void start();

    void close();

    void close(Duration timeout);

    void cleanUp();

    KafkaStreamState state();

    void restart();

    void addStateListener(BiConsumer<KafkaStreamState, KafkaStreamState> listener);

    void removeStateListener(BiConsumer<KafkaStreamState, KafkaStreamState> listener);

    void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler);

    void addShutdownHook();

    void removeShutdownHook();
}