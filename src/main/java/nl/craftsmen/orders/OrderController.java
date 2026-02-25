package nl.craftsmen.orders;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;
    private final KafkaTemplate<String, OrderEntity> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderController(
            OrderService service,
            OrderRepository repository,
            KafkaTemplate<String, OrderEntity> kafkaTemplate
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public OrderEntity placeOrder(@RequestParam String productId,
                                  @RequestParam int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }


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

        saved.setStatus("CREATED");
        kafkaTemplate.send("orders.created", saved);
        return saved;

    }

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return repository.findAll();
    }

}
