package nl.craftsmen.orders.application;

import nl.craftsmen.orders.adapters.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stock.base-url}")
    private String stockBaseUrl;


    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

}
