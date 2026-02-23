package nl.craftsmen.orders.adapters.consumer;

import nl.craftsmen.orders.adapters.publisher.KafkaProducerTestConfig;
import nl.craftsmen.orders.application.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        // Use your running Redpanda/Kafka; override from CLI if you want:
        // -Dspring.kafka.bootstrap-servers=localhost:9092
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=nl.craftsmen.orders.*",
        // ensure topics can be created (if broker allows auto-create this is harmless)
        "spring.kafka.admin.auto-create=true"
})
@Import({
        KafkaProducerTestConfig.class,
        PaymentConsumerIT.KafkaTopicTestConfig.class
})
class PaymentConsumerIT {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private KafkaTemplate<String, StatusUpdateMessage> kafkaTemplate;

    @Test
    void whenPaymentConfirmedMessageArrives_thenOrderServiceIsCalledWithMappedStatus_DONE() {

        StatusUpdateMessage message = new StatusUpdateMessage(1L, "CONFIRMED");

        kafkaTemplate.send("payments.statusupdate", message);

        verify(orderService, timeout(5_000)).setStatusAndUpdate(1L, "DONE");
    }

    @Test
    void whenPaymentInProgressMessageArrives_thenOrderServiceIsCalledWithMappedStatus_PENDING() {
        StatusUpdateMessage message = new StatusUpdateMessage(2L, "IN_PROGRESS");

        kafkaTemplate.send("payments.statusupdate", message);

        verify(orderService, timeout(5_000)).setStatusAndUpdate(2L, "PENDING");
    }

    /**
     * Ensures the topic exists on the broker for the test run.
     * If your Redpanda is configured with auto-topic-creation, this is optional but still fine.
     */
    static class KafkaTopicTestConfig {
        @Bean
        org.apache.kafka.clients.admin.NewTopic paymentsStatusUpdateTopic() {
            return TopicBuilder.name("payments.statusupdate").partitions(1).replicas(1).build();
        }

        @Bean
        KafkaAdmin.NewTopics createTopics(org.apache.kafka.clients.admin.NewTopic paymentsStatusUpdateTopic) {
            return new KafkaAdmin.NewTopics(paymentsStatusUpdateTopic);
        }
    }
}