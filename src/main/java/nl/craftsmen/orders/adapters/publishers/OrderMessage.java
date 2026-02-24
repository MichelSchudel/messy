package nl.craftsmen.orders.adapters.publishers;

public record OrderMessage(Long orderId, String statusUpdate) {}
