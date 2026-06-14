package com.jong.msaboard.support.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jong.msaboard.support.infra.config.JpaConfig;
import com.jong.msaboard.support.infra.config.RedisConfig;
import com.jong.msaboard.support.infra.service.LockTransactionalTestService;
import com.jong.msaboard.support.infra.transaction.LockTransactional;
import com.jong.msaboard.support.test.factory.EmbeddedRedisServerFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

@Slf4j
@DataJpaTest
@AutoConfigureDataRedis
@ImportAutoConfiguration(classes = {
    RedissonAutoConfigurationV2.class
})
@ContextConfiguration(classes = {
    JpaConfig.class,
    RedisConfig.class,
    LockTransactional.Advisor.class,
    LockTransactionalTest.Config.class,
    LockTransactionalTestService.class,
})
@TestPropertySource(properties = {
    "spring.data.redis.repositories.enabled=false",
    "spring.flyway.enabled=false",
    "spring.datasource.hikari.driver-class-name=org.h2.Driver",
    "spring.datasource.hikari.jdbc-url=jdbc:h2:mem:test;MODE=MySQL",
    "spring.datasource.hikari.username=sa",
    "spring.datasource.hikari.password=",
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.jpa.hibernate.properties.hibernate.hibernate.format_sql=true",
    "spring.jpa.hibernate.properties.hibernate.hibernate.use_sql_comments=true",
})
public class LockTransactionalTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.createEmbeddedRedisServer();

    @Autowired
    LockTransactionalTestService lockTransactionalTestService;

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 분산_트랜잭션_테스트() {

        var countEntityId = lockTransactionalTestService.createTestEntity();
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        var loopCount = 100;

        Runnable runnable = () -> lockTransactionalTestService.increaseTestEntityCount(countEntityId);
        CompletableFuture.allOf(IntStream.range(0, loopCount)
                .mapToObj(i -> CompletableFuture.runAsync(runnable, executor))
                .toArray(CompletableFuture[]::new))
            .join();

        assertEquals(loopCount, lockTransactionalTestService.getTestEntityCount(countEntityId));
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    static class Config {}

}
