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
    private final OrderService orderService;

    public OrderController(
            OrderService service,
            OrderRepository repository,
            KafkaTemplate<String, OrderEntity> kafkaTemplate,
            OrderService orderService
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.orderService = orderService;
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

        OrderEntity saved = orderService.placeOrder(productId, quantity);

        return saved;

    }

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return repository.findAll();
    }

}
