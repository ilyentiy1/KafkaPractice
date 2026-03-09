package com.practice.kafkatestpractice.events;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.kafkatestpractice.dto.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProcessedEvent {
    private String eventId;
    private ProcessedEventType status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private Order order;

    public static OrderProcessedEvent createNewProcessedOrderEvent(OrderEvent event) {
        return new OrderProcessedEvent(
                UUID.randomUUID().toString().substring(0,6),
                //Обработка условия из ТЗ amount > 0
                event.getOrder().getAmount() > 0 ? ProcessedEventType.ACCEPTED:ProcessedEventType.REJECTED,
                LocalDateTime.now(),
                event.getOrder()
        );
    }
}
