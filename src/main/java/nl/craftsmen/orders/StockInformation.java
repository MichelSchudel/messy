package nl.craftsmen.orders;

public record StockInformation(
        String articleId,
        StockAvailabilityType stockAvailability
) {
}
