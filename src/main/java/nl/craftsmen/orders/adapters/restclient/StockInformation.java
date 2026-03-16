package nl.craftsmen.orders.adapters.restclient;

public record StockInformation(
        String articleId,
        StockAvailabilityType stockAvailability
) {
}
