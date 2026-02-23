package nl.craftsmen.orders.application;

import nl.craftsmen.orders.OrderEntity;
import nl.craftsmen.orders.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final KafkaTemplate<String, OrderEntity> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository repository, KafkaTemplate<String, OrderEntity> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderEntity placeOrder(String productId, int quantity) {

        Boolean inStock = restTemplate.getForObject(
                "http://localhost:8089/api/stock/" + productId,
                Boolean.class
        );

        if (inStock == null || !inStock) {
            throw new RuntimeException("Product not in stock");
        }

        OrderEntity order = new OrderEntity();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(BigDecimal.valueOf(quantity * 10));
        order.setStatus("OPEN");

        OrderEntity saved = repository.save(order);

        saved.setStatus("CREATED");

        kafkaTemplate.send("orders.created", saved);
        return saved;
    }

    public void setStatusAndUpdate(OrderEntity orderEntity, String statusUpdate) {
        var statusMap = Map.of("CONFIRMED", "DONE",
                "IN_PROGRESS", "PENDING");
        orderEntity.setStatus(statusMap.get(statusUpdate));
        repository.save(orderEntity);
    }
}
