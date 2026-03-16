package nl.craftsmen.orders.application;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.adapters.repositories.OrderRepository;
import nl.craftsmen.orders.adapters.restclient.StockInformation;
import nl.craftsmen.orders.adapters.restclient.StockRequest;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static nl.craftsmen.orders.adapters.restclient.StockAvailabilityType.CODE0;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stock.base-url}")
    private String stockBaseUrl;


    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderCreatedDto createOrder(String productId, int quantity) {
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

        return createOrder(saved);
    }

    private OrderCreatedDto createOrder(OrderEntity order) {
        return new OrderCreatedDto(order.getId(), order.getStatus());
    }

    public List<Order> getAllOrders() {
        return repository.findAll().stream().map(this::fromOrderEntity).toList();
    }

    private Order fromOrderEntity(OrderEntity entity) {
        return new Order(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getTotalPrice(), entity.getStatus());
    }
}
