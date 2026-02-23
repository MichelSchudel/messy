package nl.craftsmen.orders.application.domain;

import java.math.BigDecimal;

public record Order(
        Long id,
        String productId,
        int quantity,
        BigDecimal totalPrice,
        String status
) {
    public Order withStatus(String status) {
        return new Order(id, productId, quantity, totalPrice, status);
    }
}
