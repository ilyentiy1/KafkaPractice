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
public class OrderEvent {
    private String eventId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    private Order order;


    public static OrderEvent createNewOrderEvent(String name, int amount) {
        return new OrderEvent(
                UUID.randomUUID().toString().substring(0,6),
                LocalDateTime.now(),
                new Order(name, amount)
        );
    }
}
