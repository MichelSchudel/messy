package nl.craftsmen.orders.application;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final StockAvailabilityProvider stockAvailabilityProvider;
    private final OrderProvider orderProvider;

    public OrderService(StockAvailabilityProvider stockAvailabilityProvider, OrderProvider orderProvider) {
        this.stockAvailabilityProvider = stockAvailabilityProvider;
        this.orderProvider = orderProvider;
    }

    public OrderCreatedDto createOrder(String productId, int quantity) {

        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }

        boolean inStockNow = stockAvailabilityProvider.isInStock(productId, quantity);
        if (!inStockNow) {
            throw new RuntimeException("Product not in stock");
        }

        Order order = new Order(
                null,
                productId,
                quantity,
                BigDecimal.TEN,
                "OPEN"
        );
        Order saved = orderProvider.save(order);

        return createOrder(saved);
    }

    private OrderCreatedDto createOrder(Order order) {
        return new OrderCreatedDto(order.id(), order.status());
    }

    public List<Order> getAllOrders() {
        return orderProvider.findAll();
    }

    private Order fromOrderEntity(OrderEntity entity) {
        return new Order(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getTotalPrice(), entity.getStatus());
    }
}
