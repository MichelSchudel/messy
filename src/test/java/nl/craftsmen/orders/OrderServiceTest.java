package nl.craftsmen.orders;

import nl.craftsmen.orders.application.OrderProvider;
import nl.craftsmen.orders.application.OrderService;
import nl.craftsmen.orders.application.StockAvailabilityProvider;
import nl.craftsmen.orders.application.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private StockAvailabilityProvider stockAvailabilityProvider;

    @Mock
    private OrderProvider orderProvider;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void test_quantity_is_validated() {
        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder("1", 101));
    }

    @Test
    public void test_quantity_is_validated_when_quantity_is_1() {
        when(stockAvailabilityProvider.isInStock("1", 1)).thenReturn(true);
        when(orderProvider.save(any())).thenReturn(new Order(
                1L,
                "1",
                1,
                BigDecimal.TEN,
                "OPEN"
        ));
        var orderDto = orderService.placeOrder("1", 1);
        assertThat(orderDto.status()).isEqualTo("OPEN");
    }

}