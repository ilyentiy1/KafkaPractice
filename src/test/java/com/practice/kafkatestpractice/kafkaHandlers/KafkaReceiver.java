package com.practice.kafkatestpractice.kafkaHandlers;


import com.practice.kafkatestpractice.events.OrderEvent;
import com.practice.kafkatestpractice.events.OrderProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class KafkaReceiver {

    private final ConcurrentHashMap<String, BlockingQueue<OrderEvent>>
            createdOrdersMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BlockingQueue<OrderProcessedEvent>>
            processedOrdersMap = new ConcurrentHashMap<>();

    public void registerCorrelation(String correlationId) {
        createdOrdersMap.put(correlationId, new LinkedBlockingQueue<>());
        processedOrdersMap.put(correlationId, new LinkedBlockingQueue<>());
    }

    public void unregisterCorrelation(String correlationId) {
        createdOrdersMap.remove(correlationId);
        processedOrdersMap.remove(correlationId);
    }

    @KafkaListener(
            topics = "orders.created",
            groupId = "test-group-#{T(java.util.UUID).randomUUID().toString().substring(0,5)}"
    )
    void listener(OrderEvent event) {
        log.info("Received created order message [{}] in test-group", event);
        BlockingQueue<OrderEvent> queue = createdOrdersMap.get(event.getEventId());
        if (queue != null) {
            queue.add(event);
        }
    }

    @KafkaListener(
            topics = "orders.processed",
            groupId = "test-group-#{T(java.util.UUID).randomUUID().toString().substring(0,5)}"
    )
    void listener(OrderProcessedEvent event) {
        log.info("Received processed order message [{}] in test-group", event);
        BlockingQueue<OrderProcessedEvent> queue = processedOrdersMap.get(event.getCorrelationId());
        if (queue != null) {
            queue.add(event);
        }
    }

    public OrderEvent getNextCreatedEvent(String correlationId) throws InterruptedException {
        return createdOrdersMap
                .get(correlationId)
                .poll(10, TimeUnit.SECONDS);
    }

    public OrderProcessedEvent getNextProcessedEvent(String correlationId) throws InterruptedException {
        return processedOrdersMap
                .get(correlationId)
                .poll(10, TimeUnit.SECONDS);
    }

}
