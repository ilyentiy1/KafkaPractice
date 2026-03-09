package com.practice.kafkatestpractice;

import com.practice.kafkatestpractice.config.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestKafkaTestPracticeApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(KafkaTestPracticeApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }

}
