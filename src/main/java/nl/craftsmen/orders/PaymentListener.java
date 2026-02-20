package nl.craftsmen.orders;

import jakarta.validation.Valid;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final OrderRepository repository;
    private final OrderService service;

    public PaymentListener(OrderRepository repository, OrderService service) {
        this.repository = repository;
        this.service = service;
    }

    @KafkaListener(
            topics = "payments.statusupdate",
            groupId = "orders-service"
    )    public void handlePaymentConfirmed(@Payload @Valid StatusUpdateMessage message) {
        OrderEntity order = repository.findById(message.getOrderId())
                .orElseThrow();
        service.setStatusAndUpdate(order, message.getStatusUpdate());
    }
}
