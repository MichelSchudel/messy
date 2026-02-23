package nl.craftsmen.orders.adapters.controller;

import java.math.BigDecimal;

public record OrderDto(
        Long id,
        String productId,
        int quantity,
        BigDecimal totalPrice,
        String status
) {
}
