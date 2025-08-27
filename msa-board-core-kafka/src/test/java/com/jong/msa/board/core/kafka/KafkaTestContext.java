package com.jong.msa.board.core.kafka;

import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.utils.PortUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

@Slf4j
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = PackageNames.ROOT_PACKAGE)
public class KafkaTestContext {

    public static final EmbeddedKafkaBroker EMBEDDED_KAFKA_BROKER;

    public static final int EMBEDDED_KAFKA_BROKER_PORT;

    static {

        EMBEDDED_KAFKA_BROKER_PORT = PortUtils.getAvailablePort();
        EMBEDDED_KAFKA_BROKER = new EmbeddedKafkaZKBroker(1, true, 1)
            .kafkaPorts(EMBEDDED_KAFKA_BROKER_PORT)
            .brokerProperty("auto.create.topics.enable", "false");

        String kafkaBrokerURL = "localhost:" + EMBEDDED_KAFKA_BROKER_PORT;
        System.setProperty("spring.kafka.bootstrap-servers", kafkaBrokerURL);
        System.setProperty("spring.kafka.consumer.auto-offset-reset", "earliest");
    }

    @PostConstruct
    public void init() {
        log.info("Embedded Kafka Broker Starting...");
        EMBEDDED_KAFKA_BROKER.afterPropertiesSet();
        log.info("Embedded Kafka Broker Started... (port: {})", EMBEDDED_KAFKA_BROKER_PORT);
    }

    @PreDestroy
    public void destroy() {
        log.info("Embedded Kafka Broker Stoping... (port: {})", EMBEDDED_KAFKA_BROKER_PORT);
        EMBEDDED_KAFKA_BROKER.destroy();
        log.info("Embedded Kafka Broker Stoped...");
    }

}
