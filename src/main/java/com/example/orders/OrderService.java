package com.example.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

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

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Call external inventory service directly
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
        order.setStatus("CREATED");

        OrderEntity saved = repository.save(order);

        kafkaTemplate.send("orders.created", saved);
        return saved;
    }
}
