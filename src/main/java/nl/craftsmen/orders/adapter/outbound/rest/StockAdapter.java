package nl.craftsmen.orders.adapter.outbound.rest;

import nl.craftsmen.orders.application.StockProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockAdapter implements StockProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isInStock(String productId) {
        Boolean inStock = restTemplate.getForObject(
                "http://localhost:8089/api/stock/" + productId,
                Boolean.class
        );
        return Boolean.TRUE.equals(inStock);
    }
}
