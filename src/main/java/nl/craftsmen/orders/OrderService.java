package nl.craftsmen.orders;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository repository) {
        this.repository = repository;
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

        return saved;
    }

}
