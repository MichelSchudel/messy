package nl.craftsmen.orders.adapters.controllers;

import nl.craftsmen.orders.OrderJpaRepository;
import nl.craftsmen.orders.application.OrderService;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderJpaRepository repository;
    private final OrderService orderService;

    public OrderController(
            OrderJpaRepository repository,
            OrderService orderService
    ) {
        this.repository = repository;
        this.orderService = orderService;
    }

    @PostMapping
    public Order placeOrder(@RequestParam String productId,
                            @RequestParam int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }


        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }

        Order saved = orderService.placeOrder(productId, quantity);

        return saved;

    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

}
