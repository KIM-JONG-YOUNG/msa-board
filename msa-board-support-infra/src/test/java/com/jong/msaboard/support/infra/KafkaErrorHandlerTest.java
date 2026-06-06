package com.jong.msaboard.support.infra;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.jong.msaboard.support.infra.config.KafkaConfig;
import com.jong.msaboard.support.infra.consumer.KafkaErrorConsumer;
import com.jong.msaboard.support.infra.handler.KafkaConsumerErrorHandler;
import com.jong.msaboard.support.infra.handler.KafkaProducerErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Slf4j
@SpringJUnitConfig
@ImportAutoConfiguration(classes = {
    KafkaAutoConfiguration.class,
})
@ContextConfiguration(classes = {
    KafkaConfig.class,
    KafkaConsumerErrorHandler.class,
    KafkaProducerErrorHandler.class,
    KafkaErrorConsumer.class,
})
@TestPropertySource(properties = {
    "spring.kafka.producer.acks=all",
    "spring.kafka.producer.properties.max.block.ms=3000",
    "spring.kafka.producer.properties.request.timeout.ms=30000",
    "spring.kafka.producer.properties.delivery.timeout.ms=60000",
    "spring.kafka.producer.properties.retry.backoff.ms=1000",
    "spring.kafka.consumer.group-id=test-application",
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.enable-auto-commit=false",
})
@EmbeddedKafka(
    partitions = 1, ports = 0,
    topics = "test.topic",
    brokerProperties = "auto.create.topics.enable=false"
)
public class KafkaErrorHandlerTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @MockitoSpyBean
    KafkaConsumerErrorHandler consumerErrorHandler;

    @MockitoSpyBean
    KafkaProducerErrorHandler producerErrorHandler;

    @MockitoSpyBean
    KafkaErrorConsumer kafkaErrorConsumer;

    @BeforeEach
    void beforeEach() {
        reset(producerErrorHandler);
        reset(consumerErrorHandler);
        reset(kafkaErrorConsumer);
    }

    @Test
    void 존재하지_않는_Topic_메시지_전송_테스트() {
        try {
            kafkaTemplate.send("not.exists.topic", "존재하지 않는 Topic에 전송하는 메시지입니다.");
        } catch (Exception e) {
            log.warn("Kafka 메시지 전송 간에 오류가 발생했습니다. ({})", e.getMessage());
        } finally {
            verify(producerErrorHandler).onError(any(), any(), any());
        }
    }

    @Test
    void 메시지_크기_오류_테스트() {
        try {
            var message = "a".repeat((1024 * 1024) + 1);  // 1MB 이상 메시지
            kafkaTemplate.send("test.topic", message);
        } catch (Exception e) {
            log.warn("Kafka 메시지 전송 간에 오류가 발생했습니다. ({})", e.getMessage());
        } finally {
            verify(producerErrorHandler).onError(any(), any(), any());
        }
    }

    @Test
    void 메시지_처리_오류_테스트() {

        kafkaTemplate.send("test.topic", "Producer 정상 전송 메시지입니다.");

        verify(consumerErrorHandler, timeout(5000)).handleRemaining(any(), any(), any(), any());
    }

}
