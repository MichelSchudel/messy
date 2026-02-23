package nl.craftsmen.orders.application;

import jakarta.validation.constraints.Min;
import nl.craftsmen.orders.application.domain.Order;
import nl.craftsmen.orders.application.ports.OrderProvider;
import nl.craftsmen.orders.application.ports.OrderPublisher;
import nl.craftsmen.orders.application.ports.StockProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderProvider orderProvider;
    private final StockProvider stockProvider;
    private final OrderPublisher orderPublisher;

    public OrderService(OrderProvider orderProvider, StockProvider stockProvider, OrderPublisher orderPublisher) {
        this.orderProvider = orderProvider;
        this.stockProvider = stockProvider;
        this.orderPublisher = orderPublisher;
    }

    public Order placeOrder(String productId, @Min(value = 0L) int quantity) {

        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }


        Boolean inStock = stockProvider.hasStock(productId);

        if (inStock == null || !inStock) {
            throw new RuntimeException("Product not in stock");
        }

        Order order = new Order(
                null,
                productId,
                quantity,
                BigDecimal.valueOf(quantity * 10),
                "CREATED"
        );

        Order saved = orderProvider.save(order);

        orderPublisher.publish(order);
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderProvider.findAll();
    }

    public void setStatusAndUpdate(Long orderId, String statusUpdate) {
        Order order = orderProvider.findById(orderId);
        orderProvider.save(order.withStatus(statusUpdate));
    }
}
