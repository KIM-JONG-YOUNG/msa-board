package com.jong.msaboard.support.infra.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaErrorConsumer {

    @KafkaListener(topics = "test.topic")
    public void consumeTestTopic(String message) {
        log.info("Kafka Consumer Message: {}", message);
        throw new RuntimeException("Kafka 메시지를 처리하는데 오류가 발생했습니다.");
    }

}
