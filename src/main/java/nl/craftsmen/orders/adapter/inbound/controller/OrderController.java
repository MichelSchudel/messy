package nl.craftsmen.orders.adapter.inbound.controller;

import nl.craftsmen.orders.application.OrderDto;
import nl.craftsmen.orders.application.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@RequestParam String productId,
                                    @RequestParam int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }

        return toResponse(orderService.placeOrder(productId, quantity));
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders().stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(OrderDto dto) {
        return new OrderResponse(dto.id(), dto.productId(), dto.quantity(), dto.totalPrice(), dto.status());
    }

}
