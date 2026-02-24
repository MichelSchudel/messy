package nl.craftsmen.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import nl.craftsmen.orders.adapters.consumers.StatusUpdateMessage;
import nl.craftsmen.orders.adapters.repositories.OrderEntity;
import nl.craftsmen.orders.adapters.repositories.OrderJpaRepository;
import nl.craftsmen.orders.application.domain.Order;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.EnableWireMock;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(KafkaProducerTestConfig.class)
@EnableWireMock
class OrderFlowSpringBootIT {

    @LocalServerPort
    int port;

    @Autowired
    private KafkaTemplate<String, StatusUpdateMessage> kafkaTemplate;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void placesOrder_thenPaymentStatusUpdateTopicConfirmsIt() throws Exception {

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        // Arrange: inventory says "in stock"
        String productId = "sku-123";
        WireMock.stubFor(get(urlEqualTo("/api/stock/" + productId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        // Act 1: place order via HTTP
        Order created = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders")
                        .queryParam("productId", productId)
                        .queryParam("quantity", 2)
                        .build())
                .retrieve()
                .body(Order.class);

        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo("OPEN");

        // Act 2: publish payment confirmation
        StatusUpdateMessage msg = new StatusUpdateMessage();
        msg.setOrderId(created.id());
        msg.setStatusUpdate("CONFIRMED");

        kafkaTemplate.send(
                "payments.statusupdate", msg
        ).get();

        Awaitility.await()
                .atMost(Duration.ofSeconds(1))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    OrderEntity reloaded =
                            orderJpaRepository.findById(created.id()).orElseThrow();
                    assertThat(reloaded.getStatus()).isEqualTo("DONE");
                });
    }
}