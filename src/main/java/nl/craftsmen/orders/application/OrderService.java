package nl.craftsmen.orders.application;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderStore orderStore;
    private final StockProvider stockProvider;

    public OrderService(OrderStore orderStore, StockProvider stockProvider) {
        this.orderStore = orderStore;
        this.stockProvider = stockProvider;
    }

    public OrderDto placeOrder(String productId, int quantity) {

        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot be bigger than 100");
        }

        if (!stockProvider.isInStock(productId)) {
            throw new RuntimeException("Product not in stock");
        }

        Order order = new Order(null, productId, quantity, BigDecimal.valueOf(quantity * 10), "OPEN");
        Order saved = orderStore.save(order);

        return toDto(saved);
    }

    public List<OrderDto> getAllOrders() {
        return orderStore.findAll().stream().map(this::toDto).toList();
    }

    private OrderDto toDto(Order order) {
        return new OrderDto(order.id(), order.productId(), order.quantity(), order.totalPrice(), "CREATED");
    }

}
