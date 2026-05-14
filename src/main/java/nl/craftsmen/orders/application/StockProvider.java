package nl.craftsmen.orders.application;

public interface StockProvider {
    boolean isInStock(String productId);
}
