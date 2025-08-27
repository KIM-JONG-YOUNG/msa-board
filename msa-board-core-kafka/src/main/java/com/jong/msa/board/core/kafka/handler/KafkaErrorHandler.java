package com.jong.msa.board.core.kafka.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.constants.DateTimeFormats;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaErrorHandler implements ProducerListener<String, String>, CommonErrorHandler {

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.properties.error-log-path:./kafka-error}")
    private String kafkaErrorLogPath;

    @PostConstruct
    protected void init() {
        new File(kafkaErrorLogPath).mkdirs();
    }

    private void createErrorFile(String prefix, ErrorRecord record) {
        try {
            String timestamp = DateTimeFormats.DATE_TIME_FORMATTER.format(record.timestamp());
            String fileName = prefix + "-" + timestamp.replaceAll("\\D", "") + ".log";
            String filePath = kafkaErrorLogPath + "/" + fileName;
            objectMapper.writeValue(new File(filePath), record);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 오류 파일을 생성하는데 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void onError(
        ProducerRecord<String, String> record,
        RecordMetadata metadata, Exception exception
    ) {
        log.warn("Kaka Producer 메시지 전송에 오류가 발생했습니다.", exception);
        createErrorFile("producer-error", ErrorRecord.builder()
            .topic(record.topic())
            .message(record.value())
            .exception(exception)
            .timestamp(LocalDateTime.now())
            .build());
    }

    @Override
    public boolean handleOne(
        Exception exception, ConsumerRecord<?, ?> record,
        Consumer<?, ?> consumer, MessageListenerContainer container
    ) {
        log.warn("Kaka Consumer 메시지 전송에 오류가 발생했습니다.", exception);
        createErrorFile("consumer-error", ErrorRecord.builder()
            .groupId(consumer.groupMetadata().groupId())
            .topic(record.topic())
            .message(record.value())
            .exception(exception)
            .timestamp(LocalDateTime.now())
            .build());
        return true;
    }

    @Builder
    record ErrorRecord(
        String topic,
        Object message,
        String groupId,
        Exception exception,
        LocalDateTime timestamp
    ) {}

}
