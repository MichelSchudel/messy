package nl.craftsmen.orders.application;

import nl.craftsmen.orders.application.domain.Order;

import java.util.List;

public interface OrderProvider {
    Order save(Order order);

    List<Order> findAll();
}
