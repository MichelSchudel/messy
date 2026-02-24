package nl.craftsmen.orders.adapters.publishers;

import nl.craftsmen.orders.application.domain.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderKafkaPublisher {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderKafkaPublisher(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Order order) {
        kafkaTemplate.send("orders", order);
    }
}
