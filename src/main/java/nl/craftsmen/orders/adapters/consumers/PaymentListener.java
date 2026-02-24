package nl.craftsmen.orders.adapters.consumers;

import jakarta.validation.Valid;
import nl.craftsmen.orders.StatusUpdateMessage;
import nl.craftsmen.orders.application.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentListener {

    private final OrderService service;

    public PaymentListener(OrderService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = "payments.statusupdate",
            groupId = "orders-service"
    )    public void handlePaymentConfirmed(@Payload @Valid StatusUpdateMessage message) {
        var statusMap = Map.of("CONFIRMED", "DONE",
                "IN_PROGRESS", "PENDING");
        service.setStatusAndUpdate(message.getOrderId(), statusMap.get(message.getStatusUpdate()));
    }
}
