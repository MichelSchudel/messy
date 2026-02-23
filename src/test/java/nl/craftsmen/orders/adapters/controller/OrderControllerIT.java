package nl.craftsmen.orders.adapters.controller;

import nl.craftsmen.orders.application.OrderService;
import nl.craftsmen.orders.application.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService service;

    @Test
    void postOrders_placeOrder_returnsOrderDto() throws Exception {
        Order created = new Order(
                1L,
                "sku-123",
                2,
                BigDecimal.valueOf(20),
                "CREATED"
        );

        given(service.placeOrder("sku-123", 2)).willReturn(created);

        mockMvc.perform(post("/orders")
                        .queryParam("productId", "sku-123")
                        .queryParam("quantity", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value("sku-123"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(20))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(service).placeOrder("sku-123", 2);
    }

    @Test
    void getOrders_returnsListOfOrderDtos() throws Exception {
        given(service.getAllOrders()).willReturn(List.of(
                new Order(1L, "sku-1", 1, BigDecimal.valueOf(10), "CREATED"),
                new Order(2L, "sku-2", 3, BigDecimal.valueOf(30), "DONE")
        ));

        mockMvc.perform(get("/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productId").value("sku-1"))
                .andExpect(jsonPath("$[0].quantity").value(1))
                .andExpect(jsonPath("$[0].totalPrice").value(10))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].productId").value("sku-2"))
                .andExpect(jsonPath("$[1].quantity").value(3))
                .andExpect(jsonPath("$[1].totalPrice").value(30))
                .andExpect(jsonPath("$[1].status").value("DONE"));
    }

    @Test
    void postOrders_whenQuantityIsNotPositive_returns400() throws Exception {
        mockMvc.perform(post("/orders")
                        .queryParam("productId", "sku-123")
                        .queryParam("quantity", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
