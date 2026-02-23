package nl.craftsmen.orders.adapters.consumer;

public record StatusUpdateMessage(
        Long orderId,
        String statusUpdate

) {
}
