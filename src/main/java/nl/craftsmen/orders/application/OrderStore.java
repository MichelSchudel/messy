package nl.craftsmen.orders.application;

import java.util.List;

public interface OrderStore {
    Order save(Order order);
    List<Order> findAll();
}
