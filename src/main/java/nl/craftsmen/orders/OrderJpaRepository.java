package nl.craftsmen.orders;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
