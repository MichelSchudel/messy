package nl.craftsmen.orders.adapters.consumer;

import jakarta.validation.Valid;
import nl.craftsmen.orders.application.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentConsumer {

    private final Map<String,String> map = Map.of(
            "CONFIRMED", "DONE",
            "IN_PROGRESS", "PENDING"

    );
    private final OrderService service;

    public PaymentConsumer(OrderService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = "payments.statusupdate",
            groupId = "orders-service"
    )
    public void handlePaymentConfirmed(@Payload @Valid StatusUpdateMessage message) {
        service.setStatusAndUpdate(message.orderId(), map.get(message.statusUpdate()));
    }
}
