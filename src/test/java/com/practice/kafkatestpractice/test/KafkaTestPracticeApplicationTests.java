package com.practice.kafkatestpractice.test;

import com.practice.kafkatestpractice.config.TestcontainersConfiguration;
import com.practice.kafkatestpractice.events.OrderEvent;
import com.practice.kafkatestpractice.events.OrderProcessedEvent;
import com.practice.kafkatestpractice.events.ProcessedEventType;
import com.practice.kafkatestpractice.kafkaHandlers.KafkaReceiver;
import com.practice.kafkatestpractice.kafkaHandlers.KafkaSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@DirtiesContext
class KafkaTestPracticeApplicationTests {

    @Autowired
    private KafkaSender kafkaSender;
    @Autowired
    private KafkaReceiver kafkaReceiver;


    @ParameterizedTest(name = "товар: {0}, кол-во: {1}, позитивный тест: {2}")
    @MethodSource("com.practice.kafkatestpractice.utils.CsvDataProvider#getOrderData")
    @DisplayName("Тестирование кафки")
    void pubSubTest(String name, int amount, boolean isPositive) throws InterruptedException {

        OrderEvent orderEvent = OrderEvent.createNewOrderEvent(name, amount);
        String correlationId = orderEvent.getEventId();
        //фиксация корреляционного Id для предотвращения нестабильных тестов
        kafkaReceiver.registerCorrelation(correlationId);

        try {
            //Отправление события с созданным заказом
            kafkaSender.sendMessage(
                    orderEvent,
                    "orders.created"
            );

            //Ожидание и получение события из очереди
            OrderEvent receivedOrderEvent = kafkaReceiver.getNextCreatedEvent(correlationId);

            //Отправление события после обработки полученного события
            kafkaSender.sendMessage(
                    //Обработка события производится в методе "создание нового события обработанного заказа"
                    OrderProcessedEvent.createNewProcessedOrderEvent(receivedOrderEvent),
                    "orders.processed"
            );

            //Ожидание и получение события с обработанным заказом из очереди
            OrderProcessedEvent receivedOrderProcessedEvent =
                    kafkaReceiver.getNextProcessedEvent(correlationId);

            assertNotNull(
                    receivedOrderProcessedEvent,
                    "Timeout: processed order event not received"
            );

            if(isPositive) {
                assertEquals(
                        ProcessedEventType.ACCEPTED,
                        receivedOrderProcessedEvent.getStatus()
                );
            } else {
                assertEquals(
                        ProcessedEventType.REJECTED,
                        receivedOrderProcessedEvent.getStatus()
                );
            }
        } finally {
            kafkaReceiver.unregisterCorrelation(correlationId);
        }

    }
}
