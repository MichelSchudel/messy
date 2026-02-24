package nl.craftsmen.orders.adapters.repositories;

import nl.craftsmen.orders.application.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderRepository implements nl.craftsmen.orders.application.ports.OrderProvider {

    private OrderJpaRepository repository;

    public OrderRepository(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return mapFromOrderEntity(repository.save(mapFromOrder(order)));

    }

    @Override
    public List<Order> getAllOrders() {
        return repository.findAll().stream().map(this::mapFromOrderEntity).toList();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return mapFromOrderEntity(repository.findById(orderId).orElseThrow());
    }

    public Order mapFromOrderEntity(OrderEntity orderEntity) {
        return new Order(orderEntity.getId(), orderEntity.getProductId(), orderEntity.getQuantity(), orderEntity.getTotalPrice(), orderEntity.getStatus());
    }

    public OrderEntity mapFromOrder(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(order.status());
        orderEntity.setProductId(order.productId());
        orderEntity.setQuantity(order.quantity());
        orderEntity.setTotalPrice(order.totalPrice());
        orderEntity.setId(order.id());
        return orderEntity;
    }

}
