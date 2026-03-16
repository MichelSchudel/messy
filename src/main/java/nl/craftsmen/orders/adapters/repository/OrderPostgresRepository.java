package nl.craftsmen.orders.adapters.repository;

import nl.craftsmen.orders.application.OrderProvider;
import nl.craftsmen.orders.application.domain.Order;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class OrderPostgresRepository implements OrderProvider {

    private final OrderJpaRepository repository;

    public OrderPostgresRepository(OrderJpaRepository repository) {

        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setProductId(order.productId());
        orderEntity.setQuantity(order.quantity());
        orderEntity.setTotalPrice(BigDecimal.valueOf(order.quantity() * 10));
        orderEntity.setStatus("OPEN");

        return fromEntity(repository.save(orderEntity));
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(this::fromEntity).toList();
    }

    public Order fromEntity(OrderEntity entity) {
        return new Order(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getTotalPrice(), entity.getStatus());
    }
}
