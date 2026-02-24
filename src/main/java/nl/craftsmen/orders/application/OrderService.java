package nl.craftsmen.orders.application;

import nl.craftsmen.orders.application.domain.Order;
import nl.craftsmen.orders.application.ports.OrderProvider;
import nl.craftsmen.orders.application.ports.OrderPublisher;
import nl.craftsmen.orders.application.ports.StockProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderProvider repository;

    private final StockProvider stockProvider;

    private final OrderPublisher orderPublisher;

    public OrderService(OrderProvider repository, StockProvider stockProvider, OrderPublisher orderPublisher) {
        this.repository = repository;
        this.stockProvider = stockProvider;
        this.orderPublisher = orderPublisher;
    }

    public Order placeOrder(String productId, int quantity) {

        Boolean inStock = stockProvider.isProductAvailable(productId);

        if (inStock == null || !inStock) {
            throw new RuntimeException("Product not in stock");
        }

        Order order = new Order(
                null,
                productId,
                quantity,
                BigDecimal.valueOf(quantity * 10),
                "OPEN"
        );

        Order saved = repository.save(order);

        orderPublisher.publish(saved);
        return saved;
    }

    public void setStatusAndUpdate(Long orderId, String statusUpdate) {
        Order order = repository.getOrderById(orderId);
        repository.save(order.withStatus(statusUpdate));
    }

    public List<Order> getAllOrders() {
        return repository.getAllOrders();
    }
}
