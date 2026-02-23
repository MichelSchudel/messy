package nl.craftsmen.orders.adapters.repository;

import nl.craftsmen.orders.application.domain.Order;
import nl.craftsmen.orders.application.ports.OrderProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository implements OrderProvider {

    private final OrderJpaRepository repository;

    public OrderRepository(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.id());
        orderEntity.setProductId(order.productId());
        orderEntity.setQuantity(order.quantity());
        orderEntity.setTotalPrice(order.totalPrice());
        orderEntity.setStatus(order.status());
        return map(repository.save(orderEntity));
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(this::map).toList();
    }

    @Override
    public Order findById(Long orderId) {
        return repository.findById(orderId).map(this::map).orElseThrow();
    }

    private Order map(OrderEntity orderEntity) {
        return new Order(
                orderEntity.getId(),
                orderEntity.getProductId(),
                orderEntity.getQuantity(),
                orderEntity.getTotalPrice(),
                orderEntity.getStatus()
        );
    }

}
