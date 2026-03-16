package nl.craftsmen.orders.application;

public record OrderCreatedDto(
        Long id,
        String status
) {
}
