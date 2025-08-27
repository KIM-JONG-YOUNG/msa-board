package com.jong.msa.board.core.kafka.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

public record KafkaSendEvent(String topic, String message) {

    @Component
    @RequiredArgsConstructor
    public static class Listener {

        private final KafkaTemplate<String, String> kafkaTemplate;

        @Async("kafkaSendEventExecutor")
        @TransactionalEventListener(fallbackExecution = true)
        public void listen(KafkaSendEvent event) {
            kafkaTemplate.send(event.topic(), event.message());
        }
    }

}
