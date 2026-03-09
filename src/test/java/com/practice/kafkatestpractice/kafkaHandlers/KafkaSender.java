package com.practice.kafkatestpractice.kafkaHandlers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class KafkaSender {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Object event, String topicName) {
        log.info("Sending : {}", event);
        log.info("--------------------------------");

        kafkaTemplate.send(topicName, event);
    }
}
