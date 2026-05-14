package nl.craftsmen.orders.adapter.outbound.repository;

import nl.craftsmen.orders.adapter.inbound.controller.OrderEntity;
import nl.craftsmen.orders.application.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderRepository(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Order save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setProductId(order.productId());
        entity.setQuantity(order.quantity());
        entity.setTotalPrice(order.totalPrice());
        entity.setStatus(order.status());
        return toOrder(jpaRepository.save(entity));
    }

    public List<Order> findAll() {
        return jpaRepository.findAll().stream().map(this::toOrder).toList();
    }

    private Order toOrder(OrderEntity entity) {
        return new Order(entity.getId(), entity.getProductId(), entity.getQuantity(), entity.getTotalPrice(), entity.getStatus());
    }
}
