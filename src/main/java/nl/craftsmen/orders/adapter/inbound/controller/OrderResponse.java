package nl.craftsmen.orders.adapter.inbound.controller;

import java.math.BigDecimal;

public record OrderResponse(Long id, String productId, int quantity, BigDecimal totalPrice, String status) {
}
