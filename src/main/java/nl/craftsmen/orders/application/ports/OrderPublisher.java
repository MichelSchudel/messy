package nl.craftsmen.orders.application.ports;

import nl.craftsmen.orders.application.domain.Order;

public interface OrderPublisher {
    void publish(Order order);
}
