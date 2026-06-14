package com.jong.msaboard.support.infra;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jong.msaboard.support.infra.cache.RedisCacheable;
import com.jong.msaboard.support.infra.cache.RedisEvict;
import com.jong.msaboard.support.infra.config.RedisConfig;
import com.jong.msaboard.support.infra.service.RedisCacheAnnotationTestService;
import com.jong.msaboard.support.test.factory.EmbeddedRedisServerFactory;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import redis.embedded.RedisServer;

@Slf4j
@DataRedisTest
@AutoConfigureJson
@ContextConfiguration(classes = {
    RedisConfig.class,
    RedisCacheable.Advisor.class,
    RedisEvict.Advisor.class,
    RedisCacheAnnotationTest.Config.class,
    RedisCacheAnnotationTestService.class,
})
@TestPropertySource(properties = {
    "spring.data.redis.repositories.enabled=false"
})
public class RedisCacheAnnotationTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.createEmbeddedRedisServer();

    @MockitoSpyBean
    RedisCacheAnnotationTestService redisCacheAnnotationTestService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

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
    void 캐시_저장_어노테이션_테스트() {

        redisCacheAnnotationTestService.saveToRedis(new RedisCacheAnnotationTestService.Data("value-1", "value-2", "value-3"));
        redisCacheAnnotationTestService.saveToRedis(new RedisCacheAnnotationTestService.Data("value-1", "value-2", "value-3"));

        verify(redisCacheAnnotationTestService, times(1)).saveToRedis(any());
    }

    @Test
    void 캐시_삭제_어노테이션_테스트() {

        redisTemplate.opsForValue().set("test::value-1", "value-1", Duration.ofSeconds(10));
        redisCacheAnnotationTestService.evictFromRedis(new RedisCacheAnnotationTestService.Data("value-1", "value-2", "value-3"));

        assertFalse(redisTemplate.hasKey("test::value-1"));
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    static class Config {}

}
