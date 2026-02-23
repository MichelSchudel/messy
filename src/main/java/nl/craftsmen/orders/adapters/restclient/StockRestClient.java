package nl.craftsmen.orders.adapters.restclient;

import nl.craftsmen.orders.application.ports.StockProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockRestClient implements StockProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Boolean hasStock(String productId) {

        return restTemplate.getForObject(
                "http://localhost:8089/api/stock/" + productId,
                Boolean.class
        );
    }
}
