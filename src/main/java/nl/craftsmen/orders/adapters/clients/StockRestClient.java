package nl.craftsmen.orders.adapters.clients;

import nl.craftsmen.orders.application.ports.StockProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockRestClient implements StockProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    public Boolean isProductAvailable(String productId) {
        return restTemplate.getForObject(
                "http://localhost:8089/api/stock/" + productId,
                Boolean.class
        );
    }
}
