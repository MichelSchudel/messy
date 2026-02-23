package nl.craftsmen.orders.adapters.publisher;

import nl.craftsmen.orders.application.domain.Order;
import nl.craftsmen.orders.application.ports.OrderPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderKafkaPublisher implements OrderPublisher {

    private final Map<String, String> statusMap = Map.of(
            "OPEN", "CREATED"
    );
    private final KafkaTemplate<String, OrderMessage> kafkaTemplate;

    public OrderKafkaPublisher(KafkaTemplate<String, OrderMessage> kafkaTemplate
    ) {

        this.kafkaTemplate = kafkaTemplate;

    }

    public void publish(Order order) {
        kafkaTemplate.send("orders.created", map(order));
    }

    private OrderMessage map(Order order) {
        return new OrderMessage(
                order.id(),
                order.productId(),
                order.quantity(),
                order.totalPrice(),
                map(order.status())
        );
    }

    private String map(String status) {
        return statusMap.get(status);
    }
}
