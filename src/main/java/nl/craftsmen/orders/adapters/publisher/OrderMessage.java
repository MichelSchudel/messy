package nl.craftsmen.orders.adapters.publisher;

import java.math.BigDecimal;

public record OrderMessage (
        Long id,
        String productId,
        int quantity,
        BigDecimal totalPrice,
        String status

) {
}
