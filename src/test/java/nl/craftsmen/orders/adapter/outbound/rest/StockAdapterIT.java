package nl.craftsmen.orders.adapter.outbound.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(StockAdapter.class)
@TestPropertySource(properties = "stock.base-url=http://localhost:8089")
class StockAdapterIT {

    @Autowired
    private StockAdapter stockAdapter;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void isInStock_returnsTrue_whenStockServiceSaysTrue() {
        server.expect(requestTo("http://localhost:8089/api/stock/sku-123"))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        assertThat(stockAdapter.isInStock("sku-123")).isTrue();
    }

    @Test
    void isInStock_returnsFalse_whenStockServiceSaysFalse() {
        server.expect(requestTo("http://localhost:8089/api/stock/sku-123"))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        assertThat(stockAdapter.isInStock("sku-123")).isFalse();
    }

    @Test
    void isInStock_returnsFalse_whenStockServiceReturnsNull() {
        server.expect(requestTo("http://localhost:8089/api/stock/sku-123"))
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        assertThat(stockAdapter.isInStock("sku-123")).isFalse();
    }
}
