package nl.craftsmen.orders.adapters.publishers;

import nl.craftsmen.orders.application.domain.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderKafkaPublisher implements nl.craftsmen.orders.application.ports.OrderPublisher {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderKafkaPublisher(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Order order) {
        kafkaTemplate.send("orders", order);
    }
}
