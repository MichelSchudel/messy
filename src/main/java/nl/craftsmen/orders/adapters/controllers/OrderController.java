package nl.craftsmen.orders.adapters.controllers;

import nl.craftsmen.orders.application.OrderService;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto placeOrder(@RequestParam String productId,
                            @RequestParam int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }


        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }

        Order saved = orderService.placeOrder(productId, quantity);

        return this.mapToDto(saved);

    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders().stream().map(this::mapToDto).toList();

    }

    private OrderDto mapToDto(Order order) {
        return new OrderDto(
             order.id(),
             order.productId(),
             order.quantity(),
             order.totalPrice(),
             order.status()
        );
    }

}
