package com.jong.msaboard.support.infra.config;

import com.jong.msaboard.support.infra.condition.ConditionalOnKafka;
import com.jong.msaboard.support.infra.handler.KafkaConsumerErrorHandler;
import com.jong.msaboard.support.infra.handler.KafkaProducerErrorHandler;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnKafka
public class KafkaConfig {

    @Bean
    @Primary
    ProducerFactory<String, String> kafkaProducerFactory(KafkaProperties properties) {
        var producerProperties = properties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(producerProperties, new StringSerializer(), new StringSerializer());
    }

    @Bean
    @Primary
    ConsumerFactory<String, String> kafkaConsumerFactory(KafkaProperties properties) {
        var consumerProperties = properties.buildConsumerProperties();
        return new DefaultKafkaConsumerFactory<>(consumerProperties, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    @Primary
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
        ConsumerFactory<String, String> consumerFactory,
        KafkaConsumerErrorHandler consumerErrorHandler
    ) {
        var listenerFactory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var rawListenerFactory = (ConcurrentKafkaListenerContainerFactory) listenerFactory;
        var rawConsumerFactory = (DefaultKafkaConsumerFactory) consumerFactory;
        configurer.configure(rawListenerFactory, rawConsumerFactory);
        configurer.setCommonErrorHandler(consumerErrorHandler);
        return listenerFactory;
    }

    @Bean
    @Primary
    KafkaTemplate<String, String> kafkaTemplate(
        ProducerFactory<String, String> producerFactory,
        KafkaProducerErrorHandler producerErrorHandler
    ) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setProducerListener(producerErrorHandler);
        return kafkaTemplate;
    }

}
