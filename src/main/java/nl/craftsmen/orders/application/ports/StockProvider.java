package nl.craftsmen.orders.application.ports;

public interface StockProvider {
        Boolean hasStock(String productId);

}
