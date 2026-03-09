package com.practice.kafkatestpractice.kafkaHandlers;


import com.practice.kafkatestpractice.events.OrderEvent;
import com.practice.kafkatestpractice.events.OrderProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class KafkaReceiver {

    private final BlockingQueue<OrderEvent> ordersCreatedQueue = new LinkedBlockingQueue<>();

    private final BlockingQueue<OrderProcessedEvent> ordersProcessedQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "orders.created", groupId = "test-group")
    void listener(OrderEvent event) {
        log.info("Received created order message [{}] in test-group", event);
        ordersCreatedQueue.add(event);
    }

    @KafkaListener(topics = "orders.processed", groupId = "test-group")
    void listener(OrderProcessedEvent event) {
        log.info("Received processed order message [{}] in test-group", event);
        ordersProcessedQueue.add(event);
    }

    public OrderEvent getNextCreatedEvent() throws InterruptedException {
        return ordersCreatedQueue.poll(800, TimeUnit.MILLISECONDS);
    }

    public OrderProcessedEvent getNextProcessedEvent() throws InterruptedException {
        return ordersProcessedQueue.poll(800, TimeUnit.MILLISECONDS);
    }

    public void clearAll() {
        ordersCreatedQueue.clear();
        ordersProcessedQueue.clear();
    }


}
