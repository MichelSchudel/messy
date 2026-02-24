package nl.craftsmen.orders;

import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.application.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderJpaRepository repository;

    @Mock
    private KafkaTemplate<String, OrderEntity> kafkaTemplate;

    @InjectMocks
    private OrderService service;

    @Test
    void test() {
//        service.setStatusAndUpdate(1L, "CONFIRMED");
//        when(repository.findById(1L)).thenReturn(new OrderEntity())
//        verify(repository).save(argThat(savedOrder ->
//                "DONE".equals(savedOrder.getStatus())
//        ));
    }
}