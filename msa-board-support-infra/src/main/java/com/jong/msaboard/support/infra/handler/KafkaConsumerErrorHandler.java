package com.jong.msaboard.support.infra.handler;

import com.jong.msaboard.support.infra.condition.ConditionalOnKafka;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Component
@ConditionalOnKafka
public class KafkaConsumerErrorHandler extends DefaultErrorHandler {

    public KafkaConsumerErrorHandler() {
        super(new LoggingRecoverer(), new FixedBackOff(1000L, 3));
    }

    public static class LoggingRecoverer implements ConsumerRecordRecoverer {

        @Override
        public void accept(ConsumerRecord<?, ?> record, Exception exception) {
            log.warn("""
                Kafka Consumer 메서지 처리에 오류가 발생했습니다.
                  - Topic  : {}
                  - Message: {}
                """, record.topic(), record.value(), exception);
        }
    }

}
