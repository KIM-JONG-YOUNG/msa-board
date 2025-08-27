package com.jong.msa.board.core.kafka.config;

import com.jong.msa.board.core.kafka.handler.KafkaErrorHandler;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    @Bean
    ProducerFactory<String, String> kafkaProducerFactory(KafkaProperties properties) {
        Map<String, Object> propMap = properties.buildProducerProperties();
        propMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        propMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        propMap.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        propMap.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
        propMap.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60000);
        propMap.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        propMap.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        return new DefaultKafkaProducerFactory<>(propMap);
    }

    @Bean
    ConsumerFactory<String, String> kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> propMap = properties.buildConsumerProperties();
        propMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        propMap.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        propMap.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        propMap.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        return new DefaultKafkaConsumerFactory<>(propMap);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(
        ProducerFactory<String, String> producerFactory, KafkaErrorHandler errorHandler
    ) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setProducerListener(errorHandler);
        return kafkaTemplate;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConsumerFactory<String, String> consumerFactory, KafkaErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    Executor kafkaSendEventExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
