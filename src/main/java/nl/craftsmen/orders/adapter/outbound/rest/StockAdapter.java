package nl.craftsmen.orders.adapter.outbound.rest;

import nl.craftsmen.orders.application.StockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockAdapter implements StockProvider {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StockAdapter(RestTemplateBuilder restTemplateBuilder,
                        @Value("${stock.base-url}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
    }

    public boolean isInStock(String productId) {
        Boolean inStock = restTemplate.getForObject(baseUrl + "/api/stock/" + productId, Boolean.class);
        return Boolean.TRUE.equals(inStock);
    }
}
