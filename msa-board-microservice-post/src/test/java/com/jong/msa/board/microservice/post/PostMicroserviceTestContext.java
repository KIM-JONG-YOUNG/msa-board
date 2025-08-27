package com.jong.msa.board.microservice.post;

import com.jong.msa.board.common.utils.PortUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

@Slf4j
@TestConfiguration
public class PostMicroserviceTestContext {

    public static final int EMBEDDED_REDIS_SERVER_PORT;
    public static final int EMBEDDED_KAFKA_BROKER_PORT;

    public static final RedisServer EMBEDDED_REDIS_SERVER;
    public static final EmbeddedKafkaZKBroker EMBEDDED_KAFKA_BROKER;

    static {

        EMBEDDED_REDIS_SERVER_PORT = PortUtils.getAvailablePort();
        EMBEDDED_REDIS_SERVER = RedisServer.builder()
            .redisExecProvider(RedisExecProvider.defaultProvider()
                .override(OS.MAC_OS_X, Architecture.x86_64, "redis/redis-server")
                .override(OS.MAC_OS_X, Architecture.x86, "redis/redis-server"))
            .port(EMBEDDED_REDIS_SERVER_PORT)
            .build();

        EMBEDDED_KAFKA_BROKER_PORT = PortUtils.getAvailablePort();
        EMBEDDED_KAFKA_BROKER = new EmbeddedKafkaZKBroker(1, true, 1);
        EMBEDDED_KAFKA_BROKER.kafkaPorts(EMBEDDED_KAFKA_BROKER_PORT);
        EMBEDDED_KAFKA_BROKER.brokerProperty("auto.create.topics.enable", "true");
    }

    @PostConstruct
    public void init() {
        startEmbeddedRedisServer();
        startEmbeddedKafkaBroker();
    }

    @PreDestroy
    public void destroy() {
        stopEmbeddedRedisServer();
        stopEmbeddedKafkaBroker();
    }

    private void startEmbeddedRedisServer() {
        log.info("Embedded Redis Server Starting...");
        EMBEDDED_REDIS_SERVER.start();
        log.info("Embedded Redis Server Started. (port: {})", EMBEDDED_REDIS_SERVER_PORT);
    }

    private void stopEmbeddedRedisServer() {
        log.info("Embedded Redis Server Stoping... (port: {})", EMBEDDED_REDIS_SERVER_PORT);
        EMBEDDED_REDIS_SERVER.stop();
        log.info("Embedded Redis Server Stoped.");
    }

    private void startEmbeddedKafkaBroker() {
        log.info("Embedded Kafka Broker Starting...");
        EMBEDDED_KAFKA_BROKER.afterPropertiesSet();
        log.info("Embedded Kafka Broker Started. (port: {})", EMBEDDED_KAFKA_BROKER_PORT);
    }

    private void stopEmbeddedKafkaBroker() {
        log.info("Embedded Kafka Broker Stoping... (port: {})", EMBEDDED_KAFKA_BROKER_PORT);
        EMBEDDED_KAFKA_BROKER.destroy();
        log.info("Embedded Kafka Broker Stoped.");
    }

}
