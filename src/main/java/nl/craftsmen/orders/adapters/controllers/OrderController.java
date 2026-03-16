package nl.craftsmen.orders.adapters.controllers;

import jakarta.validation.constraints.Min;
import nl.craftsmen.orders.application.OrderCreatedDto;
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
    public OrderCreateWebResponse placeOrder(@RequestParam String productId,
                                             @RequestParam @Min(1) int quantity) {

        OrderCreatedDto saved = orderService.placeOrder(productId, quantity);
        return mapFromOrderCreatedDto(saved);

    }


    private OrderCreateWebResponse mapFromOrderCreatedDto(OrderCreatedDto orderCreatedDto) {
        return new OrderCreateWebResponse(orderCreatedDto.id(), "CREATED");
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

}
