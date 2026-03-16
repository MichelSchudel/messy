package nl.craftsmen.orders;

public record StockInformation(
        String articleId,
        StockAvailabilityType stockAvailability
) {
}

enum StockAvailabilityType {
    //in stock
    CODE0,
    //out of stock
    CODE1,
    //pending stock
    CODE2,

}
