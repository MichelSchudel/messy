package nl.craftsmen.orders.application;

import nl.craftsmen.orders.application.domain.Order;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final StockAvailabilityProvider stockAvailabilityProvider;
    private final OrderProvider repository;

    public OrderService(StockAvailabilityProvider stockAvailabilityProvider, OrderProvider repository) {
        this.stockAvailabilityProvider = stockAvailabilityProvider;
        this.repository = repository;
    }

    public @NonNull OrderCreatedDto placeOrder(String productId, int quantity) {
        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }

        boolean inStock = stockAvailabilityProvider.isInStock(productId, quantity);

        if (!inStock) {
            throw new RuntimeException("Product not in stock");
        }

        Order order = new Order(
                null,
                productId,
                quantity,
                BigDecimal.valueOf(quantity * 10),
                "OPEN"
        );

        var orderSaved = repository.save(order);
        return new OrderCreatedDto(orderSaved.id(), orderSaved.status());
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }
}
