package nl.craftsmen.orders.adapters.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderJpaRepositoryIT {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    void save_persistsAndGeneratesId_thenFindByIdReturnsIt() {
        OrderEntity entity = new OrderEntity();
        entity.setProductId("sku-123");
        entity.setQuantity(2);
        entity.setTotalPrice(BigDecimal.valueOf(20));
        entity.setStatus("CREATED");

        OrderEntity saved = orderJpaRepository.save(entity);

        assertThat(saved.getId()).isNotNull();

        OrderEntity reloaded = orderJpaRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getProductId()).isEqualTo("sku-123");
        assertThat(reloaded.getQuantity()).isEqualTo(2);
        assertThat(reloaded.getTotalPrice()).isEqualByComparingTo("20");
        assertThat(reloaded.getStatus()).isEqualTo("CREATED");
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

        var all = orderJpaRepository.findAll();

        assertThat(all)
                .extracting(OrderEntity::getProductId)
                .contains("sku-a", "sku-b");
    }
}
