package nl.craftsmen.orders.application.ports;

import nl.craftsmen.orders.application.domain.Order;

import java.util.List;

public interface OrderProvider {
    Order save(Order order);

    List<Order> getAllOrders();

    Order getOrderById(Long orderId);
}
