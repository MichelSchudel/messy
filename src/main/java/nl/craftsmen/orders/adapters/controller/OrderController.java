package nl.craftsmen.orders.adapters.controller;

import jakarta.validation.constraints.Min;
import nl.craftsmen.orders.application.OrderCreatedDto;
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
    public OrderCreatedWebResponse placeOrder(@RequestParam String productId,
                                              @RequestParam @Min(1) int quantity) {

        OrderCreatedDto saved = service.createOrder(productId, quantity);
        return fromOrderCreatedDto(saved);

    }

    private OrderCreatedWebResponse fromOrderCreatedDto(OrderCreatedDto dto) {
        return new OrderCreatedWebResponse(dto.id(), "CREATED");
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

}
