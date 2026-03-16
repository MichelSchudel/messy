package nl.craftsmen.orders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

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
