package com.jong.msaboard.support.test;

import com.jong.msaboard.support.test.factory.EmbeddedRedisServerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import redis.embedded.RedisServer;

@Slf4j
@SpringJUnitConfig
public class EmbeddedRedisServerFactoryTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.createEmbeddedRedisServer();

    @Value("${spring.data.redis.host}")
    String redisHost;

    @Value("${spring.data.redis.port}")
    int redisPort;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {
        REDIS_SERVER.start();
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> REDIS_SERVER.ports().getFirst());
    }

    @AfterAll
    static void afterAll() {
        REDIS_SERVER.stop();
    }

    @Test
    void EmbeddedRedis_실행_테스트() {
        log.info("Embedded Redis Host : {}", redisHost);
        log.info("Embedded Redis Port : {}", redisPort);
    }

}
