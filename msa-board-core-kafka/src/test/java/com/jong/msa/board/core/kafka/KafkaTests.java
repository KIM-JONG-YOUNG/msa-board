package com.jong.msa.board.core.kafka;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.jong.msa.board.core.kafka.consumer.KafkaTestConsumer;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.core.kafka.handler.KafkaErrorHandler;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = KafkaTestContext.class)
public class KafkaTests {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockitoSpyBean
    private KafkaErrorHandler kafkaErrorHandler;

    @MockitoSpyBean
    private KafkaTestConsumer kafkaTestConsumer;

    @MockitoSpyBean
    private KafkaSendEvent.Listener kafkaSendEventListener;

    @BeforeAll
    void beforeAll() {
        KafkaTestContext.EMBEDDED_KAFKA_BROKER.addTopics(KafkaTestConsumer.TEST_TOPIC);
        KafkaTestContext.EMBEDDED_KAFKA_BROKER.addTopics(KafkaTestConsumer.ERROR_TOPIC);
    }

    @BeforeEach
    void beforeEach() {
        reset(kafkaErrorHandler);
        reset(kafkaTestConsumer);
        reset(kafkaSendEventListener);
    }

    @Test
    void 존재하지_않는_Topic_메세지_전송_테스트() {

        String topic = KafkaTestConsumer.NOT_EXISTS_TOPIC;
        String message = "not exists topic message";

        assertThrows(KafkaException.class, () -> kafkaTemplate.send(topic, message));
        verify(kafkaErrorHandler, timeout(3000)).onError(any(), any(), any());
    }

    @Test
    void 용량이_1MB_이상_메시지_전송_테스트() {

        String topic = KafkaTestConsumer.TEST_TOPIC;
        String message = IntStream.range(0, 2 * 1024 * 1024)
            .mapToObj(i -> "A")
            .collect(Collectors.joining());

        applicationEventPublisher.publishEvent(new KafkaSendEvent(topic, message));

        verify(kafkaErrorHandler, timeout(3000)).onError(any(), any(), any());
    }


    @Test
    void 메세지_정상_처리_테스트() {

        String topic = KafkaTestConsumer.TEST_TOPIC;
        String message = "test topic message";

        assertDoesNotThrow(() -> kafkaTemplate.send(topic, message.toString()));
        verify(kafkaTestConsumer, timeout(3000)).handleTestTopic(any());
    }

    @Test
    void 메세지_처리_중_오류_발생_테스트() {

        String topic = KafkaTestConsumer.ERROR_TOPIC;
        String message = "error topic message";

        assertDoesNotThrow(() -> kafkaTemplate.send(topic, message.toString()));
        verify(kafkaTestConsumer, timeout(3000)).handleErrorTopic(any());
        verify(kafkaErrorHandler, timeout(3000)).handleOne(any(), any(), any(), any());
    }

    @Test
    void KafkaSendEvent_테스트() {

        String topic = KafkaTestConsumer.TEST_TOPIC;
        String message = "test topic message";

        applicationEventPublisher.publishEvent(new KafkaSendEvent(topic, message));

        verify(kafkaSendEventListener, timeout(3000)).listen(any());
        verify(kafkaTestConsumer, timeout(3000)).handleTestTopic(any());
    }

}
