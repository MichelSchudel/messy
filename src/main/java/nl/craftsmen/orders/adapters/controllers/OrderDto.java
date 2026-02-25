package nl.craftsmen.orders.adapters.controllers;

import java.math.BigDecimal;

public record OrderDto(
        Long id,
        String productId,
        int quantity,
        BigDecimal totalPrice,
        String status

) {
}
