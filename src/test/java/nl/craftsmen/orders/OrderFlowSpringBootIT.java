package nl.craftsmen.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.adapters.restclient.StockAvailabilityType;
import nl.craftsmen.orders.adapters.restclient.StockInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock
@TestPropertySource(properties = {
        "stock.base-url=http://localhost:${wiremock.server.port}"
})
class OrderFlowSpringBootIT {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void placesOrder() throws Exception {

        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();

        // Arrange: inventory says "in stock"
        String productId = "sku-123";
        String stockResponse = objectMapper.writeValueAsString(new StockInformation(productId, StockAvailabilityType.CODE0));
        WireMock.stubFor(
                post(
                        urlEqualTo("/api/stock/" + productId))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(stockResponse)
                        )
        );

        //place order via HTTP
        OrderEntity created = restClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("/orders")
                                .queryParam("productId", productId)
                                .queryParam("quantity", 2)
                                .build())
                .retrieve()
                .body(OrderEntity.class);

        //assert
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo("CREATED");

    }
}