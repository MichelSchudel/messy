package nl.craftsmen.orders.adapters.controller;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.adapters.repositories.OrderRepository;
import nl.craftsmen.orders.adapters.restclient.StockInformation;
import nl.craftsmen.orders.adapters.restclient.StockRequest;
import nl.craftsmen.orders.application.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static nl.craftsmen.orders.adapters.restclient.StockAvailabilityType.CODE0;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stock.base-url}")
    private String stockBaseUrl;

    public OrderController(
            OrderService service,
            OrderRepository repository
    ) {
        this.repository = repository;
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

        StockInformation inStock = restTemplate.postForObject(
                stockBaseUrl + "/api/stock/" + productId,
                new StockRequest(
                        productId,
                        quantity
                ),
                StockInformation.class
        );

        if (inStock == null || inStock.stockAvailability() != CODE0) {
            throw new RuntimeException("Product not in stock");
        }

        OrderEntity order = new OrderEntity();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(BigDecimal.valueOf(quantity * 10));
        order.setStatus("OPEN");

        OrderEntity saved = repository.save(order);

        saved.setStatus("CREATED");
        return saved;

    }

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return repository.findAll();
    }

}
