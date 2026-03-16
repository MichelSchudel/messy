package nl.craftsmen.orders.adapters.restclient;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@RestClientTest(StockRestClient.class)
@EnableWireMock
public class StockRestClientIT {

    @Autowired
    private StockRestClient stockRestClient;

    @Test
    public void test() {
        WireMock.stubFor(get(urlEqualTo("/api/stock/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        assertThat(stockRestClient.isInStock("1", 1)).isTrue();
    }
}
