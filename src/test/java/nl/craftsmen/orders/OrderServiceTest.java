package nl.craftsmen.orders;

import nl.craftsmen.orders.adapters.repositories.OrderJpaRepository;
import nl.craftsmen.orders.application.OrderService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderJpaRepository repository;

    @InjectMocks
    private OrderService service;

}