package nl.craftsmen.orders;

import com.github.tomakehurst.wiremock.client.WireMock;
import nl.craftsmen.orders.adapters.consumer.StatusUpdateMessage;
import nl.craftsmen.orders.adapters.controller.OrderDto;
import nl.craftsmen.orders.adapters.publisher.KafkaProducerTestConfig;
import nl.craftsmen.orders.adapters.repository.OrderEntity;
import nl.craftsmen.orders.adapters.repository.OrderJpaRepository;
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
class ScenarioIT {

    @LocalServerPort
    int port;

    @Autowired
    private KafkaTemplate<String, StatusUpdateMessage> kafkaTemplate;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

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
        OrderDto created = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders")
                        .queryParam("productId", productId)
                        .queryParam("quantity", 2)
                        .build())
                .retrieve()
                .body(OrderDto.class);

        assertThat(created).isNotNull();
        assertThat(created.status()).isEqualTo("CREATED");

        // Act 2: publish payment confirmation
        StatusUpdateMessage msg = new StatusUpdateMessage(created.id(), "CONFIRMED");

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


