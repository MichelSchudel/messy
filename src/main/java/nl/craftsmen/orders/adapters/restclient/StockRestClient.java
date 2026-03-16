package nl.craftsmen.orders.adapters.restclient;

import nl.craftsmen.orders.application.StockAvailabilityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockRestClient implements StockAvailabilityProvider {

    @Value("${stock.base-url}")
    private String stockBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean isInStock(String productId, int quantity) {
        StockInformation inStock = restTemplate.postForObject(
                stockBaseUrl + "/api/stock/" + productId,
                new StockRequest(
                        productId,
                        quantity
                ),
                StockInformation.class
        );
        return StockAvailabilityType.CODE0 == inStock.stockAvailability();
    }
}
