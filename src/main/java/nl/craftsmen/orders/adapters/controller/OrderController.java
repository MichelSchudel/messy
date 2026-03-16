package nl.craftsmen.orders.adapters.controller;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.adapters.repositories.OrderRepository;
import nl.craftsmen.orders.adapters.restclient.StockInformation;
import nl.craftsmen.orders.adapters.restclient.StockRequest;
import nl.craftsmen.orders.application.OrderService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static nl.craftsmen.orders.adapters.restclient.StockAvailabilityType.CODE0;

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
    public OrderEntity placeOrder(@RequestParam String productId,
                                  @RequestParam int quantity) {

        OrderEntity saved = service.createOrder(productId, quantity);
        return saved;

    }


    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return service.getAllOrders();
    }

}
