package nl.craftsmen.orders.adapter.outbound.repository;

import nl.craftsmen.orders.adapter.inbound.controller.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
