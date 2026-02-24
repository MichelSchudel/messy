package nl.craftsmen.orders.application;

import nl.craftsmen.orders.OrderEntity;
import nl.craftsmen.orders.OrderRepository;
import nl.craftsmen.orders.application.domain.Order;
import nl.craftsmen.orders.application.ports.OrderPublisher;
import nl.craftsmen.orders.application.ports.StockProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    private final StockProvider stockProvider;

    private final OrderPublisher orderPublisher;

    public OrderService(OrderRepository repository, StockProvider stockProvider, OrderPublisher orderPublisher) {
        this.repository = repository;
        this.stockProvider = stockProvider;
        this.orderPublisher = orderPublisher;
    }

    public Order placeOrder(String productId, int quantity) {

        Boolean inStock = stockProvider.isProductAvailable(productId);

        if (inStock == null || !inStock) {
            throw new RuntimeException("Product not in stock");
        }

        OrderEntity order = new OrderEntity();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(BigDecimal.valueOf(quantity * 10));
        order.setStatus("OPEN");

        OrderEntity saved = repository.save(order);

        saved.setStatus("CREATED");

        orderPublisher.publish(mapFromOrderEntity(saved));
        return mapFromOrderEntity(saved);
    }

    public void setStatusAndUpdate(Long orderId, String statusUpdate) {
        OrderEntity orderEntity = repository.findById(orderId).orElseThrow();
        orderEntity.setStatus(statusUpdate);
        repository.save(orderEntity);
    }

    private Order mapFromOrderEntity(OrderEntity orderEntity) {
        return new Order(
                orderEntity.getId(),
                orderEntity.getProductId(),
                orderEntity.getQuantity(),
                orderEntity.getTotalPrice(),
                orderEntity.getStatus()
        );
    }

    public List<Order> getAllOrders() {
        return repository.findAll().stream().map(this::mapFromOrderEntity).toList();
    }
}
