package com.example.orders;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public OrderEntity placeOrder(@RequestParam String productId,
                                  @RequestParam int quantity) {
        return service.placeOrder(productId, quantity);
    }
}
