package com.jong.msaboard.support.infra.handler;

import com.jong.msaboard.support.infra.condition.ConditionalOnKafka;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnKafka
public class KafkaProducerErrorHandler implements ProducerListener<String, String> {

    @Override
    public void onError(
        ProducerRecord<String, String> record,
        RecordMetadata metadata,
        Exception exception
    ) {
        log.warn("""
            Kafka Producer 메서지 전송에 오류가 발생했습니다.
              - Topic  : {}
              - Message: {}
            """, record.topic(), record.value(), exception);
    }

}
