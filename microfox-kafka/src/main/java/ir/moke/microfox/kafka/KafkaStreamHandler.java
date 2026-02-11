package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaStreamState;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class KafkaStreamHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamHandler.class);
    private final String identity;

    public KafkaStreamHandler(String identity) {
        this.identity = identity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        logger.debug("Called: {}, args: {}", name, Arrays.toString(args));

        if (name.equals("toString") && method.getParameterCount() == 0)
            return proxy.getClass().getName() + "@" + System.identityHashCode(proxy);
        if (name.equals("hashCode") && method.getParameterCount() == 0)
            return System.identityHashCode(proxy);
        if (name.equals("equals") && method.getParameterCount() == 1)
            return proxy == args[0];

        KafkaStreams streams = KafkaStreamFactory.get(identity);

        switch (name) {
            case "start" -> streams.start();
            case "close" -> close(args, streams);
            case "cleanUp" -> streams.cleanUp();
            case "state" -> {
                return KafkaStreamState.valueOf(streams.state().name());
            }
            case "restart" -> restart();
            case "addStateListener" -> addStateListener(args);
            case "removeStateListener" -> removeStateListener(args);
            case "setUncaughtExceptionHandler" -> setUncaughtExceptionHandler(args, streams);
        }
        return null;
    }

    private static void setUncaughtExceptionHandler(Object[] args, KafkaStreams streams) {
        if (args[0] instanceof StreamsUncaughtExceptionHandler streamsHandler) {
            streams.setUncaughtExceptionHandler(streamsHandler);
        } else if (args[0] instanceof Thread.UncaughtExceptionHandler threadHandler) {
            Thread.setDefaultUncaughtExceptionHandler(threadHandler);
        }
    }

    @SuppressWarnings("unchecked")
    private void removeStateListener(Object[] args) {
        BiConsumer<KafkaStreamState, KafkaStreamState> listener = (BiConsumer<KafkaStreamState, KafkaStreamState>) args[0];
        KafkaStreamFactory.removeStateListener(identity, listener);
    }

    @SuppressWarnings("unchecked")
    private void addStateListener(Object[] args) {
        BiConsumer<KafkaStreamState, KafkaStreamState> listener = (BiConsumer<KafkaStreamState, KafkaStreamState>) args[0];
        KafkaStreamFactory.addStateListener(identity, listener);
    }

    private static void close(Object[] args, KafkaStreams streams) {
        if (args != null && args.length == 1 && args[0] instanceof Duration) {
            streams.close((Duration) args[0]);
        } else {
            streams.close();
        }
    }

    private void restart() {
        Logger log = LoggerFactory.getLogger(KafkaStreamHandler.class);
        log.info("Restarting KafkaStreams for identity: {}", identity);
        KafkaStreams old = KafkaStreamFactory.get(identity);
        try {
            old.close();
        } catch (Exception e) {
            log.warn("Exception while closing old streams", e);
        }
        // build a fresh instance
        KafkaStreams streams = KafkaStreamFactory.buildStreams(identity);
        KafkaStreamFactory.replaceStreams(identity, streams);
        streams.start();
    }
}

