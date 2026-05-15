package nl.craftsmen.orders.adapter.inbound.controller;

import nl.craftsmen.orders.application.OrderDto;
import nl.craftsmen.orders.application.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void placeOrder_returnsCreatedOrder() throws Exception {
        var dto = new OrderDto(1L, "sku-123", 2, BigDecimal.valueOf(20), "CREATED");
        when(orderService.placeOrder("sku-123", 2)).thenReturn(dto);

        mockMvc.perform(post("/orders")
                        .param("productId", "sku-123")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value("sku-123"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(20))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void placeOrder_withZeroQuantity_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/orders")
                        .param("productId", "sku-123")
                        .param("quantity", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrders_returnsListOfOrders() throws Exception {
        var dto1 = new OrderDto(1L, "sku-123", 2, BigDecimal.valueOf(20), "CREATED");
        var dto2 = new OrderDto(2L, "sku-456", 5, BigDecimal.valueOf(50), "CREATED");
        when(orderService.getAllOrders()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productId").value("sku-123"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].productId").value("sku-456"));
    }

    @Test
    void getAllOrders_withNoOrders_returnsEmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
