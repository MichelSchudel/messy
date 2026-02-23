package nl.craftsmen.orders.adapters.publisher;

import nl.craftsmen.orders.application.domain.Order;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.CloseOptions;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        // use your running Redpanda/Kafka (override if needed)
        // "spring.kafka.bootstrap-servers=localhost:9092",

        // for the test-consumer deserializer
        "spring.kafka.consumer.properties.spring.json.trusted.packages=nl.craftsmen.orders.*",

        // try to create the topic for the test (if broker allows it)
        "spring.kafka.admin.auto-create=true"
})
@Import(OrderKafkaPublisherIT.KafkaTopicTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderKafkaPublisherIT {

    private static final String TOPIC = "orders.created";

    @Autowired
    private OrderKafkaPublisher publisher;

    @Autowired
    private org.springframework.core.env.Environment env;

    private Consumer<String, OrderMessage> consumer;

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            CloseOptions closeOptions = CloseOptions.timeout(Duration.ofSeconds(1));

            consumer.close(closeOptions);
        }
    }

    @Test
    void publish_sendsMappedOrderMessageToOrdersCreatedTopic() {
        // Arrange
        consumer = createConsumer();
        consumer.subscribe(java.util.List.of(TOPIC));

        KafkaTestUtils.getRecords(consumer);
        Order order = new Order(
                42L,
                "sku-123",
                2,
                BigDecimal.valueOf(20),
                "OPEN"
        );

        // Act
        publisher.publish(order);

        // Assert
        ConsumerRecord<String, OrderMessage> record =
                KafkaTestUtils.getSingleRecord(consumer, TOPIC, Duration.ofSeconds(5));

        OrderMessage msg = record.value();
        assertThat(msg).isNotNull();
        assertThat(msg.id()).isEqualTo(42L);
        assertThat(msg.productId()).isEqualTo("sku-123");
        assertThat(msg.quantity()).isEqualTo(2);
        assertThat(msg.totalPrice()).isEqualByComparingTo("20");
        assertThat(msg.status()).isEqualTo("CREATED"); // mapped from OPEN -> CREATED
    }

    private Consumer<String, OrderMessage> createConsumer() {
        String bootstrapServers = env.getProperty("spring.kafka.bootstrap-servers");
        assertThat(bootstrapServers)
                .as("spring.kafka.bootstrap-servers must point to your running Redpanda/Kafka")
                .isNotBlank();

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-kafka-publisher-it-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JacksonJsonDeserializer<OrderMessage> valueDeserializer = new JacksonJsonDeserializer<>(OrderMessage.class);
        valueDeserializer.addTrustedPackages("nl.craftsmen.orders.*");

        ConsumerFactory<String, OrderMessage> cf =
                new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);

        return cf.createConsumer();
    }

    static class KafkaTopicTestConfig {

        @Bean
        NewTopic ordersCreatedTopic() {
            return TopicBuilder.name(TOPIC).partitions(1).replicas(1).build();
        }

        @Bean
        KafkaAdmin.NewTopics createTopics(NewTopic ordersCreatedTopic) {
            return new KafkaAdmin.NewTopics(ordersCreatedTopic);
        }
    }
}