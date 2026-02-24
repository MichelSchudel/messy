package nl.craftsmen.orders.application.ports;

public interface StockProvider {

    public Boolean isProductAvailable(String productId);
}
