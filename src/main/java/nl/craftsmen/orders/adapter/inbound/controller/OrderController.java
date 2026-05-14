package nl.craftsmen.orders.adapter.inbound.controller;

import nl.craftsmen.orders.adapter.outbound.repository.OrderRepository;
import nl.craftsmen.orders.application.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;
    private final OrderService orderService;

    public OrderController(
            OrderRepository repository,
            OrderService orderService
    ) {
        this.repository = repository;
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
