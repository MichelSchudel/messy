package nl.craftsmen.orders.application;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.adapters.repositories.OrderRepository;
import nl.craftsmen.orders.adapters.restclient.StockAvailabilityRestClient;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final StockAvailabilityRestClient stockAvailabilityRestClient;
    private final OrderRepository repository;

    public OrderService(StockAvailabilityRestClient stockAvailabilityRestClient, OrderRepository repository) {
        this.stockAvailabilityRestClient = stockAvailabilityRestClient;
        this.repository = repository;
    }

    public OrderCreatedDto createOrder(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }


        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }


        boolean inStockNow = stockAvailabilityRestClient.isInStock(productId, quantity);
        if (!inStockNow) {
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
