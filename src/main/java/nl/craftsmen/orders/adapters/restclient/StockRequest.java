package nl.craftsmen.orders.adapters.restclient;

public record StockRequest(
    String articeId,
    int amountWanted
) {}
