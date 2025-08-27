package com.jong.msa.board.core.redis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jong.msa.board.core.redis.annotation.RedisCacheEvict;
import com.jong.msa.board.core.redis.annotation.RedisCacheable;
import com.jong.msa.board.core.redis.config.RedisBlockingConfig;
import com.jong.msa.board.core.redis.config.RedisReactiveConfig;
import com.jong.msa.board.core.redis.service.RedisCoreTestService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Slf4j
@DirtiesContext
@SpringBootTest(
    classes = RedisTestContext.class,
    properties = "spring.data.redis.reactive=false")
public class RedisBlockingTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @MockitoSpyBean
    private RedisCoreTestService redisCoreTestService;

    @MockitoSpyBean
    private RedisCacheable.Aspect redisCacheableAspect;

    @MockitoSpyBean
    private RedisCacheEvict.Aspect redisCacheEvictAspect;

    @Test
    void 조건부_Bean_등록_테스트() {
        RedisTestContext.assertBeanExists(applicationContext, RedisBlockingConfig.class);
        RedisTestContext.assertBeanNotExists(applicationContext, RedisReactiveConfig.class);
        RedisTestContext.assertBeanExists(applicationContext, RedisCacheable.Aspect.class);
        RedisTestContext.assertBeanExists(applicationContext, RedisCacheEvict.Aspect.class);
    }

    @Test
    void RedisCacheable_테스트() {

        String key = "caching-data-key";
        String value = "caching-data-value";

        RedisCoreTestService.DTO dto = new RedisCoreTestService.DTO(key, value);
        redisCoreTestService.save(dto);
        redisCoreTestService.save(dto);

        String prefix = RedisCoreTestService.PREFIX;
        String cachingKey = prefix + key;

        verify(redisCoreTestService, times(1)).save(any());
        assertTrue(redisTemplate.hasKey(cachingKey));
    }

    @Test
    void RedisCacheEvict_테스트() {

        String key = "cache-evict-data-key";
        String value = "cache-evict-data-value";

        redisTemplate.opsForValue().set(key, value, 10, TimeUnit.SECONDS);
        redisCoreTestService.remove(key);

        String prefix = RedisCoreTestService.PREFIX;
        String cachingKey = prefix + key;

        verify(redisCacheEvictAspect).afterReturn(any());
        assertFalse(redisTemplate.hasKey(cachingKey));
    }

}
