package nl.craftsmen.orders.adapters.restclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockAvailabilityRestClient implements nl.craftsmen.orders.application.StockAvailabilityProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stock.base-url}")
    private String stockBaseUrl;

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
        return inStock != null && inStock.stockAvailability() == StockAvailabilityType.CODE0;
    }
}
