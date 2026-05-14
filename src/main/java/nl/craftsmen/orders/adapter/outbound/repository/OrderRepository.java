package nl.craftsmen.orders.adapter.outbound.repository;

import nl.craftsmen.orders.adapter.inbound.controller.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
