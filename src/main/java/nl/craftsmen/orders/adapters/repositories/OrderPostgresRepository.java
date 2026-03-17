package nl.craftsmen.orders.adapters.repositories;

import nl.craftsmen.orders.application.domain.Order;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class OrderPostgresRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderPostgresRepository(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    public Order save(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setProductId(order.productId());
        orderEntity.setQuantity(order.quantity());
        orderEntity.setTotalPrice(BigDecimal.valueOf(order.quantity() * 10));
        orderEntity.setStatus("OPEN");

        OrderEntity saved = orderJpaRepository.save(orderEntity);
        return fromOrderEntity(saved);
    }

    private Order fromOrderEntity(OrderEntity entity) {
        return new Order(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getTotalPrice(), entity.getStatus());
    }

    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream().map(this::fromOrderEntity).toList();
    }
}
