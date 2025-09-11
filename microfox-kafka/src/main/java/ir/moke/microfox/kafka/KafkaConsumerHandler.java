package ir.moke.microfox.kafka;

import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.kafka.KafkaListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KafkaConsumerHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerHandler.class);
    private static final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final String identity;
    private ScheduledFuture<?> task;

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
            case "pause" -> invokePause();
            case "resume" -> invokeResume();
            case "close" -> invokeClose();
            case "shutdown" -> invokeShutdown();
        }
        return null;
    }

    private void invokeShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(KafkaStreamFactory::closeAll, "kafka-consumer-shutdown"));
    }

    private <K, V> void invokeClose() {
        if (task != null) task.cancel(true);
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
        task = ses.scheduleWithFixedDelay(() -> consume(consumer, listener), 0, Long.parseLong(MicroFoxEnvironment.getEnv("microfox.kafka.idle.interval")), TimeUnit.MILLISECONDS);
    }

    private static <K, V> void consume(KafkaConsumer<K, V> consumer, KafkaListener<K, V> listener) {
        ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(Long.parseLong(MicroFoxEnvironment.getEnv("microfox.kafka.pool.timeout"))));
        for (ConsumerRecord<K, V> record : records) {
            try {
                Map<String, byte[]> headerMap = new HashMap<>();
                for (Header header : record.headers()) {
                    headerMap.put(header.key(), header.value());
                }
                listener.onMessage(record.topic(),
                        record.key(),
                        record.value(),
                        record.partition(),
                        record.offset(),
                        record.timestamp(),
                        record.serializedKeySize(),
                        record.serializedValueSize(),
                        headerMap,
                        record.leaderEpoch().orElse(null),
                        record.deliveryCount().orElse(null)
                );
            } catch (Exception e) {
                logger.debug("Unknown exception", e);
            }
        }
    }
}
