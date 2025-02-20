package kr.hhplus.be.server.infrastructure.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DURATION;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ConsumerFactory consumerFactory;

    @Test
    public void testKafkaSendReceive() throws InterruptedException {
        String topic = "test-topic";
        String message = "Hello Testcontainers Kafka!";

        consumerFactory.updateConfigs(Map.of(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"));

        // Create consumer
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        consumer.subscribe(List.of(topic));

        kafkaTemplate.send(topic, message);
        kafkaTemplate.flush();

        // Poll for messages
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

        consumer.close();

        // Verify messages
        assertThat(records.count()).isGreaterThan(0);
        records.forEach(record -> assertThat(record.value()).isEqualTo(message));
    }
}
