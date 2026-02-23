package nl.craftsmen.orders.adapters.controller;

import jakarta.validation.constraints.Min;
import nl.craftsmen.orders.application.OrderService;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(
            OrderService service
    ) {
        this.service = service;
    }

    @PostMapping
    public OrderDto placeOrder(@RequestParam String productId,
                               @RequestParam @Min(1) int quantity) {


        Order order = service.placeOrder(productId, quantity);
        return getOrderDto(order);

    }

    private OrderDto getOrderDto(Order order) {
        return new OrderDto(
                order.id(),
                order.productId(),
                order.quantity(),
                order.totalPrice(),
                order.status()
        );
    }


    @GetMapping
    public List<OrderDto> getAllOrders() {
        return service.getAllOrders().stream().map(this::getOrderDto).toList();
    }

}
