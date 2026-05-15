package nl.craftsmen.orders.adapter.outbound.repository;

import nl.craftsmen.orders.application.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrderRepository.class)
class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save_persistsOrderAndReturnsItWithId() {
        Order order = new Order(null, "sku-123", 2, new BigDecimal("49.99"), "PENDING");

        Order saved = orderRepository.save(order);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.productId()).isEqualTo("sku-123");
        assertThat(saved.quantity()).isEqualTo(2);
        assertThat(saved.totalPrice()).isEqualByComparingTo("49.99");
        assertThat(saved.status()).isEqualTo("PENDING");
    }

    @Test
    void findAll_returnsAllSavedOrders() {
        orderRepository.save(new Order(null, "sku-123", 1, new BigDecimal("19.99"), "PENDING"));
        orderRepository.save(new Order(null, "sku-456", 3, new BigDecimal("59.99"), "CONFIRMED"));

        List<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(Order::productId)
                .containsExactlyInAnyOrder("sku-123", "sku-456");
    }
}
