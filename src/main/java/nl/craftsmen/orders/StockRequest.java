package nl.craftsmen.orders;

public record StockRequest(
    String articeId,
    int amountWanted
) {}
