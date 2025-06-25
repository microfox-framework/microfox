package ir.moke.microfox.kafka;

import ir.moke.microfox.exception.MicrofoxException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KafkaControllerHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(KafkaControllerHandler.class);
    private final String identity;

    public KafkaControllerHandler(String identity) {
        this.identity = identity;
    }

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

        if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }

        switch (method.getName()) {
            case "send" -> invokeSend(args);
            case "batchSend" -> invokeBatchSend(args);
            case "close" -> invokeClose(args);
            case "txBegin" -> invokeTxBegin();
            case "txCommit" -> invokeTxCommit();
            case "txAbort" -> invokeTxAbort();
            case "flush" -> invokeTxFlush();
        }

        return null;
    }

    private <K, V> void invokeTxFlush() {
        KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity);
        kafkaProducer.flush();
    }

    private <K, V> void invokeTxAbort() {
        KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity);
        kafkaProducer.abortTransaction();
    }

    private <K, V> void invokeTxCommit() {
        KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity);
        kafkaProducer.commitTransaction();
    }

    private <K, V> void invokeTxBegin() {
        KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity);
        kafkaProducer.beginTransaction();
    }

    private <K, V> void invokeClose(Object[] args) {
        KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity);
        if (args != null && args.length == 0) {
            kafkaProducer.close();
        } else if (args != null) {
            kafkaProducer.close((Duration) args[0]);
        }
    }

    @SuppressWarnings("unchecked")
    private <K, V> void invokeSend(Object[] args) {
        String topic = (String) args[0];
        K key = (K) args[1];
        V value = (V) args[2];
        Integer partition = (Integer) args[3];
        Long timestamp = (Long) args[4];
        Map<String, byte[]> map = (Map<String, byte[]>) args[5];

        if (value == null) {
            throw new MicrofoxException("values is null");
        }

        Headers headers = new RecordHeaders();
        if (map != null) map.keySet().forEach(item -> headers.add(new RecordHeader(item, map.get(item))));

        try (KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity)) {
            ProducerRecord<K, V> record = new ProducerRecord<>(topic, partition, timestamp, key, value, headers);
            kafkaProducer.send(record);
            kafkaProducer.flush();
        }
    }

    @SuppressWarnings("unchecked")
    private <K, V> void invokeBatchSend(Object[] args) {
        String topic = (String) args[0];
        List<K> keys = (List<K>) args[1];
        List<V> values = (List<V>) args[2];
        Integer partition = (Integer) args[3];
        Long timestamp = (Long) args[4];
        Map<String, byte[]> map = (Map<String, byte[]>) args[5];

        if (values == null || values.isEmpty()) {
            throw new MicrofoxException("values is empty");
        }
        if (keys != null && keys.size() != values.size()) {
            throw new MicrofoxException("key and value size is not same");
        }

        Headers headers = new RecordHeaders();
        if (map != null) map.keySet().forEach(item -> headers.add(new RecordHeader(item, map.get(item))));

        try (KafkaProducer<K, V> kafkaProducer = KafkaFactory.getKafkaProducer(identity)) {
            for (int i = 0; i < values.size(); i++) {
                V value = values.get(i);
                K key = keys != null ? keys.get(i) : null;
                ProducerRecord<K, V> record = new ProducerRecord<>(topic, partition, timestamp, key, value, headers);
                kafkaProducer.send(record);
            }
            kafkaProducer.flush();
        }
    }
}
