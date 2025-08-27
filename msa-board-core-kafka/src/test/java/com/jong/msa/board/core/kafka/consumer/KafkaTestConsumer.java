package com.jong.msa.board.core.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTestConsumer {

    public static final String TEST_TOPIC = "test_topic";
    public static final String ERROR_TOPIC = "error_topic";
    public static final String NOT_EXISTS_TOPIC = "not_exists_topic";

    @KafkaListener(topics = TEST_TOPIC, groupId = "test-consumer-group")
    public void handleTestTopic(String message) {
        log.info("handleTestTopic() called with message={}", message);
    }

    @KafkaListener(topics = ERROR_TOPIC, groupId = "test-consumer-group")
    public void handleErrorTopic(String message) {
        log.info("handleErrorTopic() called with message={}", message);
        throw new RuntimeException("Kafka Consumer Runtime Error!!!");
    }

}
