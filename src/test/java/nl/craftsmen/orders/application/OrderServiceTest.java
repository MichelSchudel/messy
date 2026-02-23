package nl.craftsmen.orders.application;

import nl.craftsmen.orders.application.domain.Order;
import nl.craftsmen.orders.application.ports.OrderProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderProvider orderProvider;

    @InjectMocks
    private OrderService service;

    @Test
    void test() {
        when(orderProvider.findById(1L)).thenReturn(
                new Order(
                        1L,
                        "1",
                        1,
                        BigDecimal.TEN,
                        "CREATED"
                )
        );
        service.setStatusAndUpdate(1L, "DONE");
        verify(orderProvider).save(argThat(savedOrder ->
                "DONE".equals(savedOrder.status())
        ));
    }
}