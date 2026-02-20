package com.example.orders;

import jakarta.validation.Valid;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final OrderRepository repository;

    public PaymentListener(OrderRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "payments.statusupdate",
            groupId = "orders-service"
    )    public void handlePaymentConfirmed(@Payload @Valid StatusUpdateMessage message) {
        OrderEntity order = repository.findById(message.getOrderId())
                .orElseThrow();
        order.setStatus(message.getStatusUpdate());
        repository.save(order);
    }
}
