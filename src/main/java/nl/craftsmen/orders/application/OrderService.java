package nl.craftsmen.orders.application;

import nl.craftsmen.orders.adapter.inbound.controller.OrderEntity;
import nl.craftsmen.orders.adapter.outbound.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderDto placeOrder(String productId, int quantity) {

        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }


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

        return toDto(saved);
    }

    public List<OrderDto> getAllOrders() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    private OrderDto toDto(OrderEntity entity) {
        return new OrderDto(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getTotalPrice(), "CREATED");
    }

}
