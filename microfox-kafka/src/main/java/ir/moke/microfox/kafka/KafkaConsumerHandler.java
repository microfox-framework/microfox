package ir.moke.microfox.kafka;

import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.kafka.KafkaListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KafkaConsumerHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerHandler.class);
    private static final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final String identity;

    public KafkaConsumerHandler(String identity) {
        this.identity = identity;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        logger.debug("Called: {}, args: {}", name, Arrays.toString(args));

        if (name.equals("toString") && method.getParameterCount() == 0)
            return proxy.getClass().getName() + "@" + System.identityHashCode(proxy);
        if (name.equals("hashCode") && method.getParameterCount() == 0)
            return System.identityHashCode(proxy);
        if (name.equals("equals") && method.getParameterCount() == 1)
            return proxy == args[0];

        switch (name) {
            case "listen" -> invokeListen(args);
            case "commitAsync" -> commitAsync();
            case "commitSync" -> commitSync();
            case "pause" -> invokePause();
            case "resume" -> invokeResume();
            case "close" -> invokeClose();
            case "shutdown" -> invokeShutdown();
        }
        return null;
    }

    private <K, V> void commitSync() {
        KafkaConsumer<K, V> consumer = KafkaConsumerFactory.get(identity);
        consumer.commitSync();
    }

    private <K, V> void commitAsync() {
        KafkaConsumer<K, V> consumer = KafkaConsumerFactory.get(identity);
        consumer.commitAsync();
    }

    private void invokeShutdown() {
        ses.shutdown();
    }

    private <K, V> void invokeClose() {
        KafkaConsumerFactory.close(identity, null);
        if (!ses.isShutdown()) ses.shutdown();
    }

    private <K, V> void invokeResume() {
        KafkaConsumer<K, V> consumer = KafkaConsumerFactory.get(identity);
        consumer.resume(consumer.paused());
    }

    private <K, V> void invokePause() {
        KafkaConsumer<K, V> consumer = KafkaConsumerFactory.get(identity);
        consumer.pause(consumer.assignment());
    }

    @SuppressWarnings("unchecked")
    private <K, V> void invokeListen(Object[] args) {
        KafkaConsumer<K, V> consumer = KafkaConsumerFactory.get(identity);
        Collection<String> topics = (Collection<String>) args[0];
        KafkaListener<K, V> listener = (KafkaListener<K, V>) args[1];
        consumer.subscribe(topics);
        while (KafkaConsumerFactory.isExists(identity)) {
            consume(consumer, listener);
        }
    }

    private static <K, V> void consume(KafkaConsumer<K, V> consumer, KafkaListener<K, V> listener) {
        logger.trace("Kafka activate consumer");
        ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(Long.parseLong(MicroFoxEnvironment.getEnv("microfox.kafka.pool.timeout"))));
        records.forEach(listener::onMessage);
    }
}
