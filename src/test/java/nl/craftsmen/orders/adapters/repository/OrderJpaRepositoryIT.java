package nl.craftsmen.orders.adapters.repository;

import nl.craftsmen.orders.application.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@Import(OrderPostgresRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderJpaRepositoryIT {

    @Autowired
    private OrderPostgresRepository orderPostgresRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    void save_persistsAndGeneratesId_thenFindByIdReturnsIt() {
        Order order = new Order(
                null,
                "sku-123",
                2,
                BigDecimal.valueOf(20),
                "OPEN"
        );
        Order saved = orderPostgresRepository.save(order);

        assertThat(saved.id()).isNotNull();

        OrderEntity reloaded = orderJpaRepository.findById(saved.id()).orElseThrow();
        assertThat(reloaded.getProductId()).isEqualTo("sku-123");
        assertThat(reloaded.getQuantity()).isEqualTo(2);
        assertThat(reloaded.getTotalPrice()).isEqualByComparingTo("20");
        assertThat(reloaded.getStatus()).isEqualTo("OPEN");
    }

    @Test
    void findAll_returnsSavedOrders() {
        OrderEntity a = new OrderEntity();
        a.setProductId("sku-a");
        a.setQuantity(1);
        a.setTotalPrice(BigDecimal.valueOf(10));
        a.setStatus("CREATED");

        OrderEntity b = new OrderEntity();
        b.setProductId("sku-b");
        b.setQuantity(3);
        b.setTotalPrice(BigDecimal.valueOf(30));
        b.setStatus("CREATED");

        orderJpaRepository.save(a);
        orderJpaRepository.save(b);

        var all = orderPostgresRepository.findAll();

        assertThat(all)
                .extracting(Order::productId)
                .contains("sku-a", "sku-b");
    }
}