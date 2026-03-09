package com.practice.kafkatestpractice.test;

import com.practice.kafkatestpractice.config.TestcontainersConfiguration;
import com.practice.kafkatestpractice.events.OrderEvent;
import com.practice.kafkatestpractice.events.OrderProcessedEvent;
import com.practice.kafkatestpractice.events.ProcessedEventType;
import com.practice.kafkatestpractice.kafkaHandlers.KafkaReceiver;
import com.practice.kafkatestpractice.kafkaHandlers.KafkaSender;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@SpringBootTest

class KafkaTestPracticeApplicationTests {

    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private KafkaReceiver kafkaReceiver;

    @ParameterizedTest
    @MethodSource("com.practice.kafkatestpractice.utils.CsvDataProvider#getOrderData")
    void testKafkaIsResponsive(String name, int amount, String isPositive) throws InterruptedException {

        //Отправление события с созданным заказом
        kafkaSender.sendMessage(
                OrderEvent.createNewOrderEvent(name, amount),
                "orders.created"
        );

        //Ожидание и получение события из очереди
        OrderEvent receivedOrderEvent = kafkaReceiver.getNextCreatedEvent();

        //Отправление события после обработки полученного события
        kafkaSender.sendMessage(
                //Обработка события производится в методе "создание нового события обработанного заказа"
                OrderProcessedEvent.createNewProcessedOrderEvent(receivedOrderEvent),
                "orders.processed"
        );

        //Ожидание и получение события с обработанным заказом из очереди
        OrderProcessedEvent receivedOrderProcessedEvent = kafkaReceiver.getNextProcessedEvent();

        switch (isPositive) {
            case "true" -> assertEquals(
                    ProcessedEventType.ACCEPTED,
                    receivedOrderProcessedEvent.getStatus()
            );
            case "false" -> assertEquals(
                    ProcessedEventType.REJECTED,
                    receivedOrderProcessedEvent.getStatus()
            );
        }
    }
}
