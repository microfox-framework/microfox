import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class KafkaMainClass {
    public static void main(String[] args) {
        // Set up configuration properties
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "G1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(List.of("sample"));

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(10000));
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    System.out.println(record.key() + "     " + record.value());
                }
            }
        } finally {
            kafkaConsumer.close();
        }

    }

//    public static void main(String[] args) {
//        // Set up configuration properties
//        Properties properties = new Properties();
//
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//
//        // Create producer
//        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
//
//        // Create a producer record
//        ProducerRecord<String, String> record = new ProducerRecord<>("my-topic", "dsa");
//
//        // Send the record
//        try {
//            producer.send(record).get();
//            System.out.println("Message sent successfully");
//        } catch (Exception e) {
//            System.err.println("Error sending message: " + e.getMessage());
//        } finally {
//            producer.close();
//        }
//    }
}
