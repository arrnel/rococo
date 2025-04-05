package org.rococo.tests.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.rococo.tests.config.Config;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.util.MapWithWait;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@ParametersAreNonnullByDefault
public class KafkaConsumerService implements Runnable {

    private static final String GROUP_ID = "test";
    private static final String AUTO_OFFSET_RESET = "earliest";
    private static final Long MAX_KAFKA_TIMEOUT = 5_000L;
    private static final Config CFG = Config.getInstance();
    private static final AtomicBoolean isRun = new AtomicBoolean(false);
    private static final Properties properties = new Properties();
    private static final ObjectMapper om = new ObjectMapper();
    private static final MapWithWait<String, UserDTO> store = new MapWithWait<>();

    private final Consumer<String, String> consumer = new KafkaConsumer<>(properties);
    private final List<String> topics;

    static {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    }

    public KafkaConsumerService() {
        this(CFG.kafkaTopics());
    }

    public KafkaConsumerService(List<String> kafkaTopics) {
        this.topics = kafkaTopics;
    }

    public static UserDTO getUser(String username) throws InterruptedException {
        return store.get(username, MAX_KAFKA_TIMEOUT);
    }

    @Override
    public void run() {
        try {
            isRun.set(true);
            consumer.subscribe(topics);
            while (isRun.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    var userAsString = consumerRecord.value();
                    var user = om.readValue(userAsString, UserDTO.class);
                    store.put(user.getUsername(), user);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            consumer.close();
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        isRun.set(false);
    }
}
