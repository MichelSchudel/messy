package nl.craftsmen.orders.application;

import java.math.BigDecimal;

public record Order(Long id, String productId, int quantity, BigDecimal totalPrice, String status) {
}
