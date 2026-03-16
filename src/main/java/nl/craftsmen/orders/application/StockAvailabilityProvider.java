package nl.craftsmen.orders.application;

public interface StockAvailabilityProvider {
    boolean isInStock(String productId, int quantity);
}
